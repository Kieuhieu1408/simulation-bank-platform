package com.hieu.corebank.transfer;

import com.hieu.corebank.account.api.dto.request.AccountCreateRequestDTO;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.application.command.CreateAccountCommandHandler;
import com.hieu.corebank.customer.application.command.CreateCustomerCommand;
import com.hieu.corebank.customer.application.command.CreateCustomerCommandHandler;
import com.hieu.corebank.transfer.api.dto.request.TransferRequestDTO;
import com.hieu.corebank.transfer.application.command.TransferHandler;
import com.hieu.corebank.transfer.application.saga.SagaMetricsCollector;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SagaObservabilityIntegrationTest {

    @Autowired
    private CreateCustomerCommandHandler createCustomerHandler;

    @Autowired
    private CreateAccountCommandHandler createAccountHandler;

    @Autowired
    private TransferHandler transferHandler;

    @Autowired
    private SagaMetricsCollector sagaMetricsCollector;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    @DisplayName("NFR-CB-18 / D16: Observability Saga - Các metrics Micrometer được đăng ký và ghi nhận chính xác")
    void testSagaMetricsRegisteredAndRecorded() {
        String cif1 = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(new CreateCustomerCommand(cif1));
        AccountResponseDTO src = createAccountHandler.handle(
                new AccountCreateRequestDTO(cif1, "VND", new BigDecimal("500000.00")).toCommand());

        String cif2 = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(new CreateCustomerCommand(cif2));
        AccountResponseDTO dst = createAccountHandler.handle(
                new AccountCreateRequestDTO(cif2, "VND", new BigDecimal("200000.00")).toCommand());

        // Thực hiện transfer thành công
        String idempotencyKey = "metrics-tx-" + UUID.randomUUID();
        TransferRequestDTO cmd = TransferRequestDTO.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("100000.00"))
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Metrics test")
                .build();

        transferHandler.handle(cmd);

        // 1. Kiểm tra metric corebank_saga_step_total
        Counter reserveSuccessCounter = meterRegistry.find("corebank_saga_step_total")
                .tag("step", "reserve")
                .tag("status", "success")
                .counter();
        assertThat(reserveSuccessCounter).isNotNull();
        assertThat(reserveSuccessCounter.count()).isGreaterThanOrEqualTo(1.0);

        Counter completeSuccessCounter = meterRegistry.find("corebank_saga_step_total")
                .tag("step", "complete")
                .tag("status", "success")
                .counter();
        assertThat(completeSuccessCounter).isNotNull();
        assertThat(completeSuccessCounter.count()).isGreaterThanOrEqualTo(1.0);

        // 2. Kiểm tra metric corebank_saga_stuck_total
        sagaMetricsCollector.recordStuckTransfers(3);
        Gauge stuckGauge = meterRegistry.find("corebank_saga_stuck_total").gauge();
        assertThat(stuckGauge).isNotNull();
        assertThat(stuckGauge.value()).isEqualTo(3.0);

        // 3. Kiểm tra metric corebank_concurrency_retries_total
        sagaMetricsCollector.recordConcurrencyRetry();
        Counter retryCounter = meterRegistry.find("corebank_concurrency_retries_total").counter();
        assertThat(retryCounter).isNotNull();
        assertThat(retryCounter.count()).isGreaterThanOrEqualTo(1.0);
    }
}
