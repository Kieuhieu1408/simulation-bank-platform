package com.hieu.corebank.eventsourcing.aggregate;

import com.hieu.common.constant.AccountStatus;
import com.hieu.corebank.eventsourcing.event.*;
import com.hieu.corebank.exception.BusinessException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AccountAggregate — trái tim của Write Side.
 *
 * <p>KHÔNG phải JPA @Entity. Không lưu vào database trực tiếp.
 * Trạng thái (actualBalance, availableBalance) chỉ tồn tại trên RAM,
 * được tính bằng cách Replay (chạy lại) chuỗi DomainEvent.
 *
 * <p>Quy tắc bất biến:
 * <ul>
 *   <li>handle() — nhận Command, validate, sinh ra Event MỚI vào uncommittedEvents.
 *   <li>apply()  — nhận Event (cũ hoặc mới), thay đổi trạng thái trên RAM.
 * </ul>
 */
public class AccountAggregate {

    // ── Identity ─────────────────────────────────────────────────────────────
    private String        accountId;
    private String        accountNumber;
    private String        customerId;
    private String        currency;

    // ── Mutable State (trên RAM, không phải DB) ───────────────────────────────
    private BigDecimal    actualBalance;    // Số dư sổ cái
    private BigDecimal    availableBalance; // Số dư khả dụng
    private AccountStatus status;
    private long          version;         // Phiên bản hiện tại (dùng cho Optimistic Lock)

    // ── Uncommitted Events (chờ lưu vào Event Store) ─────────────────────────
    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();

    // ── Snapshot threshold ────────────────────────────────────────────────────
    public static final int SNAPSHOT_THRESHOLD = 50;

    protected AccountAggregate() {}

    // =========================================================================
    // Factory — tạo mới (sinh AccountCreatedEvent)
    // =========================================================================

    public static AccountAggregate create(String accountId, String accountNumber,
                                          String customerId, String currency,
                                          BigDecimal initialBalance) {
        AccountAggregate agg = new AccountAggregate();
        agg.applyNew(new AccountCreatedEvent(accountId, 1L,
                accountNumber, customerId, currency, initialBalance));
        return agg;
    }

    // =========================================================================
    // Reconstitute — khôi phục từ Event history (Replay)
    // =========================================================================

    public static AccountAggregate reconstitute(List<DomainEvent> history) {
        AccountAggregate agg = new AccountAggregate();
        for (DomainEvent event : history) {
            agg.applyHistory(event);
        }
        return agg;
    }

    // =========================================================================
    // Commands — validate và sinh Event
    // =========================================================================

    /**
     * Giữ tiền để chuẩn bị chuyển khoản.
     * Thay thế hoàn toàn logic PESSIMISTIC_WRITE trong V1.
     */
    public void reserveFunds(BigDecimal amount, String currency, String transferId) {
        if (status != AccountStatus.ACTIVE) {
            throw new BusinessException("Account is not ACTIVE: " + accountId);
        }
        if (!this.currency.equals(currency.toUpperCase())) {
            throw new BusinessException("Currency mismatch: expected " + this.currency);
        }
        if (availableBalance.compareTo(amount) < 0) {
            throw new BusinessException("Insufficient available balance");
        }
        applyNew(new FundsReservedEvent(accountId, nextVersion(), amount, currency, transferId));
    }

    /**
     * Xác nhận chuyển khoản thành công — trừ actual balance.
     */
    public void completeTransfer(BigDecimal amount, String currency,
                                 String transferId, String destinationAccountId) {
        applyNew(new TransferCompletedEvent(accountId, nextVersion(),
                amount, currency, transferId, destinationAccountId));
    }

    /**
     * Hoàn trả tiền đã giữ khi chuyển khoản thất bại.
     */
    public void failTransfer(BigDecimal amount, String currency,
                             String transferId, String reason) {
        applyNew(new TransferFailedEvent(accountId, nextVersion(),
                amount, currency, transferId, reason));
    }

    /**
     * Nhận tiền từ chuyển khoản đến.
     */
    public void depositFunds(BigDecimal amount, String currency, String sourceTransferId) {
        if (status != AccountStatus.ACTIVE) {
            throw new BusinessException("Destination account is not ACTIVE: " + accountId);
        }
        if (!this.currency.equals(currency.toUpperCase())) {
            throw new BusinessException("Currency mismatch on destination: expected " + this.currency);
        }
        applyNew(new FundsDepositedEvent(accountId, nextVersion(),
                amount, currency, sourceTransferId));
    }

    // =========================================================================
    // Apply — thay đổi trạng thái RAM dựa trên Event (cũ hoặc mới)
    // =========================================================================

    private void apply(DomainEvent event) {
        if (event instanceof AccountCreatedEvent e) {
            this.accountId        = e.aggregateId();
            this.accountNumber    = e.accountNumber();
            this.customerId       = e.customerId();
            this.currency         = e.currency();
            this.actualBalance    = e.initialBalance();
            this.availableBalance = e.initialBalance();
            this.status           = AccountStatus.ACTIVE;

        } else if (event instanceof FundsReservedEvent e) {
            this.availableBalance = this.availableBalance.subtract(e.amount());

        } else if (event instanceof TransferCompletedEvent e) {
            this.actualBalance    = this.actualBalance.subtract(e.amount());

        } else if (event instanceof TransferFailedEvent e) {
            this.availableBalance = this.availableBalance.add(e.amount());

        } else if (event instanceof FundsDepositedEvent e) {
            this.actualBalance    = this.actualBalance.add(e.amount());
            this.availableBalance = this.availableBalance.add(e.amount());
        }
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private void applyNew(DomainEvent event) {
        apply(event);
        this.version = event.version();
        uncommittedEvents.add(event);
    }

    private void applyHistory(DomainEvent event) {
        apply(event);
        this.version = event.version();
    }

    private long nextVersion() {
        return this.version + 1;
    }

    // =========================================================================
    // Getters
    // =========================================================================

    public String        accountId()        { return accountId; }
    public String        accountNumber()    { return accountNumber; }
    public String        customerId()       { return customerId; }
    public String        currency()         { return currency; }
    public BigDecimal    actualBalance()    { return actualBalance; }
    public BigDecimal    availableBalance() { return availableBalance; }
    public AccountStatus status()           { return status; }
    public long          version()          { return version; }
    public boolean       shouldSnapshot()   { return version % SNAPSHOT_THRESHOLD == 0; }

    public List<DomainEvent> uncommittedEvents() {
        return Collections.unmodifiableList(uncommittedEvents);
    }

    public void markCommitted() {
        uncommittedEvents.clear();
    }
}