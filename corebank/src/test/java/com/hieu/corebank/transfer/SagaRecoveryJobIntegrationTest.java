package com.hieu.corebank.transfer;

import com.hieu.corebank.account.api.dto.request.AccountCreateRequestDTO;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.application.command.CreateAccountCommandHandler;
import com.hieu.corebank.account.infrastructure.projection.AccountView;
import com.hieu.corebank.account.infrastructure.projection.AccountViewRepository;
import com.hieu.corebank.customer.application.command.CreateCustomerCommand;
import com.hieu.corebank.customer.application.command.CreateCustomerCommandHandler;
import com.hieu.corebank.shared.domain.*;
import com.hieu.corebank.transfer.application.port.AccountGateway;
import com.hieu.corebank.transfer.application.saga.SagaRecoveryJob;
import com.hieu.corebank.transfer.domain.Transfer;
import com.hieu.corebank.transfer.domain.TransferRepository;
import com.hieu.corebank.transfer.domain.TransferStatus;
import com.hieu.corebank.transfer.infrastructure.projection.TransferView;
import com.hieu.corebank.transfer.infrastructure.projection.TransferViewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SagaRecoveryJobIntegrationTest {

    @Autowired
    private CreateCustomerCommandHandler createCustomerHandler;

    @Autowired
    private CreateAccountCommandHandler createAccountHandler;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private TransferViewRepository transferViewRepository;

    @Autowired
    private AccountGateway accountGateway;

    @Autowired
    private AccountViewRepository accountViewRepository;

    @Autowired
    private SagaRecoveryJob sagaRecoveryJob;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    @DisplayName("AC-CB-06: Tự phục hồi Saga sau sự cố Pod crash tại bước DESTINATION_CREDITED (Self-Healing)")
    void testRecoveryOfStuckTransferAtDestinationCredited() {
        // 1. Tạo 2 tài khoản nguồn & đích
        String cif1 = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(new CreateCustomerCommand(cif1));
        AccountResponseDTO src = createAccountHandler.handle(
                new AccountCreateRequestDTO(cif1, "VND", new BigDecimal("1000000.00")).toCommand());

        String cif2 = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(new CreateCustomerCommand(cif2));
        AccountResponseDTO dst = createAccountHandler.handle(
                new AccountCreateRequestDTO(cif2, "VND", new BigDecimal("500000.00")).toCommand());

        TransferId transferId = TransferId.of("stuck-tx-" + UUID.randomUUID());
        Money amount = Money.of(new BigDecimal("200000.00"), Currency.of("VND"));

        // 2. Giả lập Pod crash: Thực hiện đến bước DESTINATION_CREDITED rồi dừng (chưa kịp settle)
        transactionTemplate.execute(status -> {
            Transfer transfer = Transfer.initiate(
                    transferId,
                    AccountId.of(src.getAccountId()),
                    AccountId.of(dst.getAccountId()),
                    amount,
                    "Simulated crash transfer",
                    "hash123"
            );
            transferRepository.save(transfer);
            return null;
        });

        // Step 1: Reserve source
        accountGateway.reserveFunds(AccountId.of(src.getAccountId()), transferId, amount);
        transactionTemplate.execute(status -> {
            Transfer t = transferRepository.findById(transferId).orElseThrow();
            t.markSourceReserved();
            transferRepository.save(t);
            return null;
        });

        // Step 2: Credit destination (PIVOT POINT)
        accountGateway.creditFunds(AccountId.of(dst.getAccountId()), transferId, amount);
        transactionTemplate.execute(status -> {
            Transfer t = transferRepository.findById(transferId).orElseThrow();
            t.markDestinationCredited();
            transferRepository.save(t);
            return null;
        });

        // === POD CRASH GIẢ LẬP TẠI ĐÂY ===
        // Kiểm tra hiện trạng khi bị crash:
        TransferView viewBeforeRecovery = transferViewRepository.findById(transferId.value()).orElseThrow();
        assertThat(viewBeforeRecovery.getStatus()).isEqualTo(TransferStatus.DESTINATION_CREDITED.name());

        // Bên nhận đã có 500k + 200k = 700k
        AccountView dstViewBefore = accountViewRepository.findById(dst.getAccountId()).orElseThrow();
        assertThat(dstViewBefore.getActualBalance()).isEqualByComparingTo(new BigDecimal("700000.00"));

        // Bên gửi: available đã trừ (800k), nhưng actual vẫn là 1,000,000 do chưa settle!
        AccountView srcViewBefore = accountViewRepository.findById(src.getAccountId()).orElseThrow();
        assertThat(srcViewBefore.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("800000.00"));
        assertThat(srcViewBefore.getActualBalance()).isEqualByComparingTo(new BigDecimal("1000000.00"));

        // 3. Giả lập transaction đã bị treo 35 giây trước (vượt quá ngưỡng stuckThresholdSeconds=30s)
        viewBeforeRecovery.setUpdatedAt(Instant.now().minus(35, ChronoUnit.SECONDS));
        transferViewRepository.save(viewBeforeRecovery);

        // 4. Kích hoạt SagaRecoveryJob quét và tự phục hồi
        sagaRecoveryJob.recoverStuckTransfers();

        // 5. Kiểm tra kết quả sau tự phục hồi:
        TransferView viewAfterRecovery = transferViewRepository.findById(transferId.value()).orElseThrow();
        assertThat(viewAfterRecovery.getStatus()).isEqualTo("COMPLETED");

        // Bên gửi: đã settle thành công -> actualBalance và availableBalance đều là 800,000 VND
        AccountView srcViewAfter = accountViewRepository.findById(src.getAccountId()).orElseThrow();
        assertThat(srcViewAfter.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("800000.00"));
        assertThat(srcViewAfter.getActualBalance()).isEqualByComparingTo(new BigDecimal("800000.00"));

        // Bên nhận: vẫn là 700,000 VND (không bị credit lặp lại)
        AccountView dstViewAfter = accountViewRepository.findById(dst.getAccountId()).orElseThrow();
        assertThat(dstViewAfter.getActualBalance()).isEqualByComparingTo(new BigDecimal("700000.00"));
    }
}
