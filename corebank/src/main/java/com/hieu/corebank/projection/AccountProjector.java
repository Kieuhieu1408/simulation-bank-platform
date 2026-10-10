package com.hieu.corebank.projection;

import com.hieu.corebank.eventsourcing.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

import java.util.UUID;

/**
 * AccountProjector — đồng bộ Read Side từ Domain Events.
 *
 * <p>Nguyên tắc vàng của Projector:
 * <ul>
 *   <li>Chạy trong cùng DB transaction với EventStore.append() → sub-ms delay</li>
 *   <li>Mọi mutator trên AccountView đều có idempotency guard (lastEventVersion check)</li>
 *   <li>Nếu Projector fail, toàn bộ transaction rollback → Write Side và Read Side luôn đồng bộ</li>
 * </ul>
 *
 * <p>Trong giai đoạn này dùng Spring {@code @EventListener} (synchronous, in-process).
 * Khi nâng cấp lên Kafka, chỉ cần đổi annotation thành {@code @KafkaListener} và
 * xử lý idempotency qua {@code last_event_version} — logic bên trong không đổi.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountProjector {

    private final AccountViewRepository         accountViews;
    private final TransactionHistoryViewRepository historyViews;

    // ── AccountCreated ────────────────────────────────────────────────────────

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    @Transactional
    public void on(AccountCreatedEvent event) {
        // Idempotency: nếu view đã tồn tại thì bỏ qua
        if (accountViews.existsById(event.aggregateId())) {
            log.warn("AccountView already exists, skipping AccountCreatedEvent: accountId={}",
                    event.aggregateId());
            return;
        }

        AccountView view = AccountView.create(
                event.aggregateId(),
                event.accountNumber(),
                event.customerId(),
                event.currency(),
                event.initialBalance(),
                event.version()
        );
        accountViews.save(view);

        log.debug("Projected AccountCreated: accountId={}", event.aggregateId());
    }

    // ── FundsReserved ─────────────────────────────────────────────────────────
 
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    @Transactional
    public void on(FundsReservedEvent event) {
        AccountView view = loadOrWarn(event.aggregateId());
        if (view == null) return;

        view.reserveFunds(event.amount(), event.version());
        accountViews.save(view);

        // Ghi vào lịch sử giao dịch: DEBIT (tiền đang đi ra)
        historyViews.save(new TransactionHistoryView(
                UUID.randomUUID().toString(),
                event.aggregateId(),
                "DEBIT",
                event.amount(),
                event.currency(),
                view.getAvailableBalance(),   // balance_after = available sau khi trừ
                event.eventType(),
                "Transfer reserved: " + event.transferId(),
                event.occurredAt()
        ));

        log.debug("Projected FundsReserved: accountId={} amount={}", event.aggregateId(), event.amount());
    }

    // ── TransferCompleted ─────────────────────────────────────────────────────

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    @Transactional
    public void on(TransferCompletedEvent event) {
        AccountView view = loadOrWarn(event.aggregateId());
        if (view == null) return;

        view.completeTransfer(event.amount(), event.version());
        accountViews.save(view);

        log.debug("Projected TransferCompleted: accountId={} amount={}", event.aggregateId(), event.amount());
    }

    // ── TransferFailed ────────────────────────────────────────────────────────

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    @Transactional
    public void on(TransferFailedEvent event) {
        AccountView view = loadOrWarn(event.aggregateId());
        if (view == null) return;

        view.rollbackReservation(event.amount(), event.version());
        accountViews.save(view);

        // Ghi cancellation vào lịch sử
        historyViews.save(new TransactionHistoryView(
                UUID.randomUUID().toString(),
                event.aggregateId(),
                "DEBIT",   // direction tetap DEBIT nhưng đây là reversal
                event.amount(),
                event.currency(),
                view.getAvailableBalance(),
                event.eventType(),
                "Transfer failed, funds released: " + event.reason(),
                event.occurredAt()
        ));

        log.debug("Projected TransferFailed: accountId={} reason={}", event.aggregateId(), event.reason());
    }

    // ── FundsDeposited ────────────────────────────────────────────────────────

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    @Transactional
    public void on(FundsDepositedEvent event) {
        AccountView view = loadOrWarn(event.aggregateId());
        if (view == null) return;

        view.depositFunds(event.amount(), event.version());
        accountViews.save(view);

        // Ghi vào lịch sử: CREDIT (tiền vào)
        historyViews.save(new TransactionHistoryView(
                UUID.randomUUID().toString(),
                event.aggregateId(),
                "CREDIT",
                event.amount(),
                event.currency(),
                view.getActualBalance(),   // balance_after = actual sau khi cộng
                event.eventType(),
                "Transfer received: " + event.sourceTransferId(),
                event.occurredAt()
        ));

        log.debug("Projected FundsDeposited: accountId={} amount={}", event.aggregateId(), event.amount());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private AccountView loadOrWarn(String accountId) {
        return accountViews.findById(accountId).orElseGet(() -> {
            log.warn("AccountView not found for event, skipping: accountId={}", accountId);
            return null;
        });
    }
}