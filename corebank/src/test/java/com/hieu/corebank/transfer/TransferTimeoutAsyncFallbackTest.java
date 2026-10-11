package com.hieu.corebank.transfer;

import com.hieu.common.constant.TransactionStatus;
import com.hieu.corebank.account.api.dto.request.AccountCreateRequestDTO;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.application.command.CreateAccountCommandHandler;
import com.hieu.corebank.account.infrastructure.projection.AccountView;
import com.hieu.corebank.account.infrastructure.projection.AccountViewRepository;
import com.hieu.corebank.customer.application.command.CreateCustomerCommand;
import com.hieu.corebank.customer.application.command.CreateCustomerCommandHandler;
import com.hieu.corebank.transfer.api.TransferController;
import com.hieu.corebank.transfer.api.dto.request.TransferRequestDTO;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import com.hieu.corebank.transfer.application.port.AccountGateway;
import com.hieu.corebank.transfer.infrastructure.projection.TransferView;
import com.hieu.corebank.transfer.infrastructure.projection.TransferViewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "corebank.transfer.sync-timeout-seconds=1"
})
class TransferTimeoutAsyncFallbackTest {

    @Autowired
    private CreateCustomerCommandHandler createCustomerHandler;

    @Autowired
    private CreateAccountCommandHandler createAccountHandler;

    @Autowired
    private TransferController transferController;

    @Autowired
    private TransferViewRepository transferViewRepository;

    @Autowired
    private AccountViewRepository accountViewRepository;

    @MockitoSpyBean
    private AccountGateway accountGateway;

    @MockitoSpyBean
    private com.hieu.corebank.access.security.PermissionCacheService permissionCacheService;

    @org.junit.jupiter.api.BeforeEach
    void setUpSecurity() {
        org.mockito.Mockito.doReturn(true).when(permissionCacheService)
                .hasAnyPermission(any(), any(), any());
        org.springframework.security.authentication.TestingAuthenticationToken auth =
                new org.springframework.security.authentication.TestingAuthenticationToken("service-test", "pwd", "ROLE_SERVICE_MONEYBANK");
        auth.setAuthenticated(true);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("AC-CB-09: Saga vượt quá sync-timeout -> API trả về HTTP 202 Accepted PENDING, giao dịch tiếp tục chạy ngầm tới COMPLETED")
    void testTimeoutAsyncFallback202Accepted() throws Exception {
        String cif1 = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(new CreateCustomerCommand(cif1));
        AccountResponseDTO src = createAccountHandler.handle(
                new AccountCreateRequestDTO(cif1, "VND", new BigDecimal("1000000.00")).toCommand());

        String cif2 = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(new CreateCustomerCommand(cif2));
        AccountResponseDTO dst = createAccountHandler.handle(
                new AccountCreateRequestDTO(cif2, "VND", new BigDecimal("500000.00")).toCommand());

        // Giả lập trễ mạng 1.8 giây tại bước reserve (vượt quá sync-timeout 1s)
        doAnswer(invocation -> {
            Thread.sleep(1800);
            return invocation.callRealMethod();
        }).when(accountGateway).reserveFunds(any(), any(), any());

        String idempotencyKey = "timeout-tx-" + UUID.randomUUID();
        TransferRequestDTO request = TransferRequestDTO.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("200000.00"))
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Slow transfer test")
                .build();

        // Gọi API POST /transfers
        long start = System.currentTimeMillis();
        ResponseEntity<TransferResponseDTO> response = transferController.transfer(request);
        long elapsed = System.currentTimeMillis() - start;

        // Verify: Trả về HTTP 202 Accepted và status=PENDING
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(response.getBody().getTransactionId()).isNotBlank();
        assertThat(elapsed).isLessThan(2500L); // Không bị treo vĩnh viễn

        String transferId = response.getBody().getTransactionId();

        // Chờ tác vụ ngầm trong nền hoàn tất (sau 1.8s sleep + saga execution)
        for (int i = 0; i < 20; i++) {
            Thread.sleep(200);
            TransferView view = transferViewRepository.findById(transferId).orElse(null);
            if (view != null && "COMPLETED".equals(view.getStatus())) {
                break;
            }
        }

        // Verify: Giao dịch ngầm đã hoàn tất COMPLETED thành công!
        TransferView finalView = transferViewRepository.findById(transferId).orElseThrow();
        assertThat(finalView.getStatus()).isEqualTo("COMPLETED");

        // Verify số dư cả 2 tài khoản sau khi hoàn tất ngầm
        AccountView srcView = accountViewRepository.findById(src.getAccountId()).orElseThrow();
        assertThat(srcView.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("800000.00"));
        assertThat(srcView.getActualBalance()).isEqualByComparingTo(new BigDecimal("800000.00"));

        AccountView dstView = accountViewRepository.findById(dst.getAccountId()).orElseThrow();
        assertThat(dstView.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("700000.00"));
        assertThat(dstView.getActualBalance()).isEqualByComparingTo(new BigDecimal("700000.00"));
    }
}
