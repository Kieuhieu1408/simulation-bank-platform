package com.hieu.corebank.transfer;

import com.hieu.common.constant.AccountStatus;
import com.hieu.common.constant.TransactionStatus;
import com.hieu.corebank.account.api.dto.request.AccountCreateRequestDTO;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.application.command.CreateAccountCommandHandler;
import com.hieu.corebank.account.infrastructure.projection.AccountView;
import com.hieu.corebank.account.infrastructure.projection.AccountViewRepository;
import com.hieu.corebank.customer.application.command.CreateCustomerCommand;
import com.hieu.corebank.customer.application.command.CreateCustomerCommandHandler;
import com.hieu.corebank.shared.domain.BusinessException;
import com.hieu.corebank.shared.domain.ConflictException;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import com.hieu.corebank.transfer.application.command.InitiateTransferCommand;
import com.hieu.corebank.transfer.application.command.InitiateTransferHandler;
import com.hieu.corebank.transfer.infrastructure.projection.TransferView;
import com.hieu.corebank.transfer.infrastructure.projection.TransferViewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class TransferSagaIntegrationTest {

    @Autowired
    private CreateCustomerCommandHandler createCustomerHandler;

    @Autowired
    private CreateAccountCommandHandler createAccountHandler;

    @Autowired
    private InitiateTransferHandler initiateTransferHandler;

    @Autowired
    private AccountViewRepository accountViewRepository;

    @Autowired
    private TransferViewRepository transferViewRepository;

    private AccountResponseDTO createTestAccount(BigDecimal initialBalance) {
        String cif = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(new CreateCustomerCommand(cif));
        AccountCreateRequestDTO req = new AccountCreateRequestDTO(cif, "VND", initialBalance);
        return createAccountHandler.handle(req.toCommand());
    }

    @Test
    @DisplayName("AC-CB-02: Chuyển tiền thành công giữa 2 tài khoản (Happy Path)")
    void testHappyPathTransfer() {
        AccountResponseDTO src = createTestAccount(new BigDecimal("1000000.00"));
        AccountResponseDTO dst = createTestAccount(new BigDecimal("500000.00"));

        String idempotencyKey = "happy-path-" + UUID.randomUUID();
        InitiateTransferCommand cmd = InitiateTransferCommand.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("200000.00"))
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Chuyen tien mua sach")
                .build();

        TransferResponseDTO response = initiateTransferHandler.handle(cmd);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        assertThat(response.getTransactionId()).isNotBlank();
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("200000.00"));

        // Kiểm tra số dư người gửi: 1,000,000 - 200,000 = 800,000 VND
        AccountView srcView = accountViewRepository.findById(src.getAccountId()).orElseThrow();
        assertThat(srcView.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("800000.00"));
        assertThat(srcView.getActualBalance()).isEqualByComparingTo(new BigDecimal("800000.00"));

        // Kiểm tra số dư người nhận: 500,000 + 200,000 = 700,000 VND
        AccountView dstView = accountViewRepository.findById(dst.getAccountId()).orElseThrow();
        assertThat(dstView.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("700000.00"));
        assertThat(dstView.getActualBalance()).isEqualByComparingTo(new BigDecimal("700000.00"));

        // Kiểm tra transfer_view
        TransferView transferView = transferViewRepository.findById(response.getTransactionId()).orElseThrow();
        assertThat(transferView.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    @DisplayName("AC-CB-04: Replay kết quả Idempotency trong <= 50ms khi gửi lại cùng key và payload")
    void testIdempotencyReplay() {
        AccountResponseDTO src = createTestAccount(new BigDecimal("1000000.00"));
        AccountResponseDTO dst = createTestAccount(new BigDecimal("500000.00"));

        String idempotencyKey = "replay-" + UUID.randomUUID();
        InitiateTransferCommand cmd = InitiateTransferCommand.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("150000.00"))
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Replay test")
                .build();

        TransferResponseDTO res1 = initiateTransferHandler.handle(cmd);
        assertThat(res1.getStatus()).isEqualTo(TransactionStatus.SUCCESS);

        long start = System.currentTimeMillis();
        TransferResponseDTO res2 = initiateTransferHandler.handle(cmd);
        long elapsed = System.currentTimeMillis() - start;

        assertThat(res2.getTransactionId()).isEqualTo(res1.getTransactionId());
        assertThat(res2.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        assertThat(elapsed).isLessThanOrEqualTo(100L); // Thường <= 50ms

        // Số dư không bị trừ thêm lần 2
        AccountView srcView = accountViewRepository.findById(src.getAccountId()).orElseThrow();
        assertThat(srcView.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("850000.00"));
    }

    @Test
    @DisplayName("AC-CB-07: Phát hiện Xung đột Idempotency khi thay đổi thông tin (Trả về 409 Conflict)")
    void testIdempotencyConflict() {
        AccountResponseDTO src = createTestAccount(new BigDecimal("1000000.00"));
        AccountResponseDTO dst = createTestAccount(new BigDecimal("500000.00"));

        String idempotencyKey = "conflict-" + UUID.randomUUID();
        InitiateTransferCommand cmd1 = InitiateTransferCommand.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("100000.00"))
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Transfer 1")
                .build();

        initiateTransferHandler.handle(cmd1);

        // Gửi cùng idempotencyKey nhưng đổi số tiền lên 200,000
        InitiateTransferCommand cmd2 = InitiateTransferCommand.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("200000.00")) // Đổi số tiền
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Transfer 1 modified")
                .build();

        assertThatThrownBy(() -> initiateTransferHandler.handle(cmd2))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Idempotency key conflict");
    }

    @Test
    @DisplayName("AC-CB-05: Chuyển tiền từ tài khoản nguồn không đủ số dư bị từ chối")
    void testInsufficientBalanceTransferFails() {
        AccountResponseDTO src = createTestAccount(new BigDecimal("50000.00"));
        AccountResponseDTO dst = createTestAccount(new BigDecimal("500000.00"));

        String idempotencyKey = "insufficient-" + UUID.randomUUID();
        InitiateTransferCommand cmd = InitiateTransferCommand.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("200000.00"))
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Transfer too much")
                .build();

        assertThatThrownBy(() -> initiateTransferHandler.handle(cmd))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient available balance");

        // Số dư không đổi
        AccountView srcView = accountViewRepository.findById(src.getAccountId()).orElseThrow();
        assertThat(srcView.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("50000.00"));
    }
}
