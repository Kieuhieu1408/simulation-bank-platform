package com.hieu.corebank.account.domain;

import com.hieu.common.constant.AccountStatus;
import com.hieu.corebank.account.domain.event.*;
import com.hieu.corebank.shared.domain.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.hieu.corebank.shared.domain.Currency;

/**
 * AccountAggregate — Aggregate Root đại diện cho Tài khoản Thanh toán (Event Sourcing).
 *
 * <p>Quy tắc kiến trúc DDD:
 * <ul>
 *   <li>Thuần khiết Domain (không phụ thuộc Spring, JPA, Jackson).
 *   <li>Thẩm định Invariant Cấp độ 3 (Domain Invariants):
 *       Tài khoản phải ACTIVE; availableBalance >= amount; kiểm tra trùng lặp Idempotency trên từng giao dịch.
 *   <li>Áp dụng Value Objects: {@link AccountId}, {@link AccountNumber}, {@link CustomerId},
 *       {@link Currency}, {@link Money}, {@link TransferId}.
 *   <li>Quản lý danh sách các khoản phong tỏa {@link Hold} nội tại.
 * </ul>
 */
public class AccountAggregate extends AggregateRoot<AccountId> {

    // ── Identity ─────────────────────────────────────────────────────────────
    private AccountId     accountId;
    private AccountNumber accountNumber;
    private CustomerId    customerId;
    private Currency      currency;

    // ── Mutable State (trên RAM, tái tạo qua Event Sourcing) ──────────────────
    private Money         actualBalance;    // Số dư sổ cái (Ledger Balance)
    private Money         availableBalance; // Số dư khả dụng
    private AccountStatus status;

    // ── Holds & Idempotency Tracking (Cấp 3 Invariants) ───────────────────────
    private final Map<String, Hold> holds = new HashMap<>();
    private final Set<String>       creditedTransfers = new HashSet<>();

    // ── Snapshot threshold ────────────────────────────────────────────────────
    public static final int SNAPSHOT_THRESHOLD = 50;

    protected AccountAggregate() {}

    // =========================================================================
    // Factory — tạo mới (sinh AccountCreatedEvent)
    // =========================================================================

    public static AccountAggregate create(AccountId accountId, AccountNumber accountNumber,
                                          CustomerId customerId, Currency currency,
                                          Money initialBalance) {
        if (accountId == null || accountNumber == null || customerId == null || currency == null) {
            throw new DomainException("Account creation attributes cannot be null");
        }
        Money init = initialBalance == null ? Money.zero(currency) : initialBalance;
        init.requireNonNegative();

        AccountAggregate agg = new AccountAggregate();
        agg.applyNew(new AccountCreatedEvent(
                accountId.value(),
                1L,
                accountNumber.value(),
                customerId.value(),
                currency.code(),
                init.amount()
        ));
        return agg;
    }

    public static AccountAggregate create(String accountId, String accountNumber,
                                          String customerId, String currency,
                                          BigDecimal initialBalance) {
        Currency curr = Currency.of(currency);
        Money initial = initialBalance == null ? Money.zero(curr) : Money.of(initialBalance, curr);
        return create(
                AccountId.of(accountId),
                AccountNumber.of(accountNumber),
                CustomerId.of(customerId),
                curr,
                initial
        );
    }

    // =========================================================================
    // Reconstitute — nạp từ lịch sử Event (Replay)
    // =========================================================================

    public static AccountAggregate reconstitute(List<DomainEvent> history) {
        if (history == null || history.isEmpty()) {
            throw new IllegalArgumentException("History cannot be empty");
        }
        AccountAggregate agg = new AccountAggregate();
        for (DomainEvent event : history) {
            agg.apply(event);
            agg.version = event.version();
        }
        return agg;
    }

    // =========================================================================
    // Cấp 3 Invariants & Idempotent Business Methods
    // =========================================================================

    /**
     * Giữ tiền cho giao dịch chuyển đi (Bên gửi).
     *
     * <p>Idempotency Invariant: Nếu transferId đã có Hold ở trạng thái HELD/SETTLED/RELEASED -> no-op an toàn.
     */
    public void reserve(TransferId transferId, Money amount) {
        assertActive();
        assertCurrency(amount.currency());
        amount.requirePositive();

        // 1. Kiểm tra Idempotency nội tại
        Hold existingHold = holds.get(transferId.value());
        if (existingHold != null) {
            // Đã được giữ trước đó -> no-op
            return;
        }

        // 2. Thẩm định Invariant: Số dư khả dụng
        if (availableBalance.isLessThan(amount)) {
            throw new BusinessException(String.format(
                    "Insufficient available balance. Required: %s, Available: %s",
                    amount, availableBalance));
        }

        applyNew(new FundsReservedEvent(
                accountId.value(),
                nextVersion(),
                amount.amount(),
                amount.currency().code(),
                transferId.value()
        ));
    }

    /**
     * Ghi có cho giao dịch chuyển đến (Bên nhận - Điểm Pivot).
     *
     * <p>Idempotency Invariant: Nếu transferId đã từng được credit -> no-op an toàn.
     */
    public void credit(TransferId transferId, Money amount) {
        assertActive();
        assertCurrency(amount.currency());
        amount.requirePositive();

        // 1. Kiểm tra Idempotency nội tại
        if (creditedTransfers.contains(transferId.value())) {
            // Đã được credit trước đó -> no-op
            return;
        }

        applyNew(new FundsDepositedEvent(
                accountId.value(),
                nextVersion(),
                amount.amount(),
                amount.currency().code(),
                transferId.value()
        ));
    }

    /**
     * Quyết toán phong tỏa — Bên nhận đã nhận thành công.
     * Sổ cái chính thức trừ tiền (actualBalance -= amount).
     *
     * <p>Idempotency Invariant: Nếu Hold đã SETTLED -> no-op; nếu Hold chưa tồn tại -> ném lỗi.
     * Lưu ý: Không kiểm tra assertActive() vì đây là quyết toán khoản hold đã được giữ trước đó khi tài khoản còn active.
     */
    public void settle(TransferId transferId) {
        Hold hold = holds.get(transferId.value());
        if (hold == null) {
            throw new BusinessException("Hold not found for transfer: " + transferId.value());
        }
        if (hold.isSettled()) {
            // Đã settle trước đó -> no-op
            return;
        }
        if (hold.isReleased()) {
            throw new BusinessException("Hold already released for transfer: " + transferId.value());
        }

        applyNew(new TransferCompletedEvent(
                accountId.value(),
                nextVersion(),
                hold.amount().amount(),
                hold.amount().currency().code(),
                transferId.value(),
                ""
        ));
    }

    /**
     * Giải tỏa phong tỏa (Bù trừ / Compensating Transaction) — Giao dịch chuyển tiền thất bại.
     * Hoàn lại số tiền vào availableBalance (availableBalance += amount).
     *
     * <p>Idempotency Invariant: Nếu Hold đã RELEASED -> no-op.
     */
    public void release(TransferId transferId, String reason) {
        Hold hold = holds.get(transferId.value());
        if (hold == null) {
            throw new BusinessException("Hold not found for transfer: " + transferId.value());
        }
        if (hold.isReleased()) {
            // Đã release trước đó -> no-op
            return;
        }
        if (hold.isSettled()) {
            throw new BusinessException("Hold already settled for transfer: " + transferId.value());
        }

        applyNew(new TransferFailedEvent(
                accountId.value(),
                nextVersion(),
                hold.amount().amount(),
                hold.amount().currency().code(),
                transferId.value(),
                reason
        ));
    }

    // =========================================================================
    // Overload Helpers (Duy trì tính tương thích ngược cho codebase hiện tại)
    // =========================================================================

    public void reserveFunds(BigDecimal amount, String currencyCode, String transferId) {
        reserve(TransferId.of(transferId), Money.of(amount, Currency.of(currencyCode)));
    }

    public void depositFunds(BigDecimal amount, String currencyCode, String sourceTransferId) {
        credit(TransferId.of(sourceTransferId), Money.of(amount, Currency.of(currencyCode)));
    }

    public void completeTransfer(BigDecimal amount, String currencyCode,
                                 String transferId, String destinationAccountId) {
        settle(TransferId.of(transferId));
    }

    public void failTransfer(BigDecimal amount, String currencyCode,
                             String transferId, String reason) {
        release(TransferId.of(transferId), reason);
    }

    // =========================================================================
    // Event Application (State Mutators — CHỈ cập nhật RAM, KHÔNG ném BusinessException)
    // =========================================================================

    @Override
    protected void apply(DomainEvent event) {
        switch (event) {
            case AccountSnapshotRestoredEvent e -> {
                this.accountId        = AccountId.of(e.aggregateId());
                this.accountNumber    = AccountNumber.of(e.accountNumber());
                this.customerId       = CustomerId.of(e.customerId());
                this.currency         = Currency.of(e.currency());
                this.actualBalance    = Money.of(e.actualBalance(), this.currency);
                this.availableBalance = Money.of(e.availableBalance(), this.currency);
                this.status           = AccountStatus.valueOf(e.status());
                this.holds.clear();
                for (AccountSnapshotRestoredEvent.HoldSnapshot h : e.holds()) {
                    this.holds.put(h.transferId(), new Hold(
                            TransferId.of(h.transferId()),
                            Money.of(h.amount(), this.currency),
                            HoldStatus.valueOf(h.status()),
                            h.createdAt()
                    ));
                }
                this.creditedTransfers.clear();
                this.creditedTransfers.addAll(e.creditedTransfers());
            }
            case AccountCreatedEvent e -> {
                this.accountId        = AccountId.of(e.aggregateId());
                this.accountNumber    = AccountNumber.of(e.accountNumber());
                this.customerId       = CustomerId.of(e.customerId());
                this.currency         = Currency.of(e.currency());
                this.actualBalance    = Money.of(e.initialBalance(), this.currency);
                this.availableBalance = Money.of(e.initialBalance(), this.currency);
                this.status           = AccountStatus.ACTIVE;
                this.holds.clear();
                this.creditedTransfers.clear();
            }
            case FundsReservedEvent e -> {
                Money money = Money.of(e.amount(), this.currency);
                this.availableBalance = this.availableBalance.subtract(money);
                Hold hold = new Hold(TransferId.of(e.transferId()), money, HoldStatus.HELD, e.occurredAt());
                this.holds.put(e.transferId(), hold);
            }
            case TransferCompletedEvent e -> {
                Money money = Money.of(e.amount(), this.currency);
                this.actualBalance = this.actualBalance.subtract(money);
                Hold hold = this.holds.get(e.transferId());
                if (hold != null) {
                    this.holds.put(e.transferId(), hold.settle());
                }
            }
            case TransferFailedEvent e -> {
                Money money = Money.of(e.amount(), this.currency);
                this.availableBalance = this.availableBalance.add(money);
                Hold hold = this.holds.get(e.transferId());
                if (hold != null) {
                    this.holds.put(e.transferId(), hold.release());
                }
            }
            case FundsDepositedEvent e -> {
                Money money = Money.of(e.amount(), this.currency);
                this.actualBalance    = this.actualBalance.add(money);
                this.availableBalance = this.availableBalance.add(money);
                this.creditedTransfers.add(e.sourceTransferId());
            }
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getName());
        }
    }

    // =========================================================================
    // Invariants & Guards
    // =========================================================================

    private void assertActive() {
        if (this.status != AccountStatus.ACTIVE) {
            throw new BusinessException("Account is not active: status=" + this.status);
        }
    }

    private void assertCurrency(Currency requestedCurrency) {
        if (!this.currency.equals(requestedCurrency)) {
            throw new BusinessException(String.format(
                    "Currency mismatch: account uses %s, requested %s",
                    this.currency.code(), requestedCurrency.code()));
        }
    }

    // =========================================================================
    // Getters & Lifecycle hooks
    // =========================================================================

    @Override
    public AccountId id() {
        return accountId;
    }

    public String accountId() {
        return accountId != null ? accountId.value() : null;
    }

    public String accountNumber() {
        return accountNumber != null ? accountNumber.value() : null;
    }

    public String customerId() {
        return customerId != null ? customerId.value() : null;
    }

    public String currency() {
        return currency != null ? currency.code() : null;
    }

    public BigDecimal actualBalance() {
        return actualBalance != null ? actualBalance.amount() : null;
    }

    public BigDecimal availableBalance() {
        return availableBalance != null ? availableBalance.amount() : null;
    }

    public Money actualBalanceMoney() {
        return actualBalance;
    }

    public Money availableBalanceMoney() {
        return availableBalance;
    }

    public AccountStatus status() {
        return status;
    }

    public Map<String, Hold> holds() {
        return Collections.unmodifiableMap(holds);
    }

    public Set<String> creditedTransfers() {
        return Collections.unmodifiableSet(creditedTransfers);
    }

    public boolean shouldSnapshot() {
        return this.version % SNAPSHOT_THRESHOLD == 0;
    }
}
