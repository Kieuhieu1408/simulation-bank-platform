package com.hieu.corebank.transfer.application.saga;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * SagaMetricsCollector — Quản lý các Micrometer metrics phục vụ giám sát vận hành Saga (NFR-CB-18, D16).
 *
 * <ul>
 *   <li>{@code corebank_saga_step_total{step, status}}: Đếm số lượng và trạng thái các bước thực thi saga.
 *   <li>{@code corebank_saga_stuck_total}: Số lượng giao dịch bị kẹt ở trạng thái non-terminal quá ngưỡng thời gian.
 *   <li>{@code corebank_concurrency_retries_total}: Đếm số lần retry do xung đột phiên bản Optimistic Lock.
 * </ul>
 */
@Component
public class SagaMetricsCollector {

    private final MeterRegistry meterRegistry;
    private final AtomicInteger stuckTransfersCount = new AtomicInteger(0);
    private final Counter concurrencyRetriesCounter;

    public SagaMetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.concurrencyRetriesCounter = Counter.builder("corebank_concurrency_retries_total")
                .description("Total number of concurrency retries")
                .register(meterRegistry);
        Gauge.builder("corebank_saga_stuck_total", stuckTransfersCount, AtomicInteger::get)
                .description("Current number of stuck saga transfers")
                .register(meterRegistry);
    }

    public void recordStep(String step, String status) {
        Counter.builder("corebank_saga_step_total")
                .tag("step", step)
                .tag("status", status)
                .description("Total number of saga step executions")
                .register(meterRegistry)
                .increment();
    }

    public void recordStuckTransfers(int count) {
        stuckTransfersCount.set(count);
    }

    public void recordConcurrencyRetry() {
        concurrencyRetriesCounter.increment();
    }

}
