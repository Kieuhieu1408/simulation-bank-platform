package com.hieu.corebank.projection;

import com.hieu.corebank.constant.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Read Model — account_view.
 *
 * <p>Đây là nguồn sự thật cho chiều Query (Mobile App, Dashboard).
 * KHÔNG dùng để validate Command. KHÔNG có logic nghiệp vụ.
 *
 * <p>Được cập nhật bởi {@link AccountProjector} khi nhận Domain Event.
 * Cột last_event_version đảm bảo idempotency: Projector chỉ apply event
 * nếu event.version > last_event_version.
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "account_view")
public class AccountView {

    @Id
    @Column(name = "account_id", length = 50, nullable = false)
    private String accountId;

    @Column(name = "account_number", length = 20, nullable = false)
    private String accountNumber;

    @Column(name = "customer_id", length = 20, nullable = false)
    private String customerId;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "actual_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal actualBalance;

    @Column(name = "available_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal availableBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private AccountStatus status;

    @Column(name = "last_event_version", nullable = false)
    private long lastEventVersion;

    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;

    // ── Factory ──────────────────────────────────────────────────────────────

    public static AccountView create(String accountId, String accountNumber,
                                     String customerId, String currency,
                                     BigDecimal initialBalance, long version) {
        AccountView view = new AccountView();
        view.accountId          = accountId;
        view.accountNumber      = accountNumber;
        view.customerId         = customerId;
        view.currency           = currency;
        view.actualBalance      = initialBalance;
        view.availableBalance   = initialBalance;
        view.status             = AccountStatus.ACTIVE;
        view.lastEventVersion   = version;
        view.lastUpdatedAt      = Instant.now();
        return view;
    }

    // ── Mutators (chỉ Projector được gọi) ───────────────────────────────────

    public void reserveFunds(BigDecimal amount, long eventVersion) {
        if (eventVersion <= this.lastEventVersion) return; // idempotency guard
        this.availableBalance = this.availableBalance.subtract(amount);
        this.lastEventVersion = eventVersion;
        this.lastUpdatedAt    = Instant.now();
    }

    public void completeTransfer(BigDecimal amount, long eventVersion) {
        if (eventVersion <= this.lastEventVersion) return;
        this.actualBalance    = this.actualBalance.subtract(amount);
        this.lastEventVersion = eventVersion;
        this.lastUpdatedAt    = Instant.now();
    }

    public void rollbackReservation(BigDecimal amount, long eventVersion) {
        if (eventVersion <= this.lastEventVersion) return;
        this.availableBalance = this.availableBalance.add(amount);
        this.lastEventVersion = eventVersion;
        this.lastUpdatedAt    = Instant.now();
    }

    public void depositFunds(BigDecimal amount, long eventVersion) {
        if (eventVersion <= this.lastEventVersion) return;
        this.actualBalance    = this.actualBalance.add(amount);
        this.availableBalance = this.availableBalance.add(amount);
        this.lastEventVersion = eventVersion;
        this.lastUpdatedAt    = Instant.now();
    }
}