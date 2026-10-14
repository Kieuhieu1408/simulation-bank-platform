package com.hieu.corebank.transfer.infrastructure.account;

import com.hieu.corebank.account.application.command.AccountHandler;
import com.hieu.corebank.shared.domain.AccountId;
import com.hieu.corebank.shared.domain.Money;
import com.hieu.corebank.shared.domain.TransferId;
import com.hieu.corebank.transfer.application.port.AccountGateway;
import com.hieu.corebank.transfer.application.saga.SagaMetricsCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * InProcessAccountGateway — Adapter hiện thực hóa AccountGateway trong cùng tiến trình (Modular Monolith).
 *
 * <p>Được trang bị cơ chế tự động thử lại (Retry) khi phát hiện xung đột khóa lạc quan
 * (Optimistic Lock conflict - {@link DataIntegrityViolationException}), kết hợp đo lường
 * chỉ số {@code corebank_concurrency_retries_total}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InProcessAccountGateway implements AccountGateway {

    private static final int MAX_RETRIES = 10;

    private final AccountHandler accountHandler;
    private final SagaMetricsCollector metricsCollector;

    @Override
    public void reserveFunds(AccountId accountId, TransferId transferId, Money amount) {
        executeWithRetry(() -> accountHandler.reserveFunds(accountId, transferId, amount));
    }

    @Override
    public void creditFunds(AccountId accountId, TransferId transferId, Money amount) {
        executeWithRetry(() -> accountHandler.creditFunds(accountId, transferId, amount));
    }

    @Override
    public void settleReservation(AccountId accountId, TransferId transferId) {
        executeWithRetry(() -> accountHandler.settleReservation(accountId, transferId));
    }

    @Override
    public void releaseReservation(AccountId accountId, TransferId transferId, String reason) {
        executeWithRetry(() -> accountHandler.releaseReservation(accountId, transferId, reason));
    }

    private void executeWithRetry(Runnable action) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                action.run();
                return;
            } catch (DataIntegrityViolationException ex) {
                metricsCollector.recordConcurrencyRetry();
                if (attempt == MAX_RETRIES) {
                    log.error("Exceeded max retries ({}) due to concurrency conflict: {}", MAX_RETRIES, ex.getMessage());
                    throw ex;
                }
                log.warn("Concurrency conflict on attempt {}/{}, retrying...", attempt, MAX_RETRIES);
                try {
                    Thread.sleep(10L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }
    }
}
