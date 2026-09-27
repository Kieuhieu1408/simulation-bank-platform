package com.hieu.corebank.projection;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Read Model — transaction_history_view.
 *
 * <p>Sao kê hiển thị cho User (Statement).
 * Cột balance_after cho phép User thấy số dư sau mỗi giao dịch — đặc trưng ngân hàng.
 */
@Getter
@NoArgsConstructor
@Entity
@Table(
    name = "transaction_history_view",
    indexes = @Index(name = "ix_txhv_account_date", columnList = "account_id, occurred_at DESC")
)
public class TransactionHistoryView {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "account_id", length = 50, nullable = false)
    private String accountId;

    @Column(name = "direction", length = 6, nullable = false)
    private String direction; // DEBIT | CREDIT

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "balance_after", nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceAfter;

    @Column(name = "event_type", length = 100, nullable = false)
    private String eventType;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    public TransactionHistoryView(String id, String accountId, String direction,
                                  BigDecimal amount, String currency,
                                  BigDecimal balanceAfter, String eventType,
                                  String description, Instant occurredAt) {
        this.id           = id;
        this.accountId    = accountId;
        this.direction    = direction;
        this.amount       = amount;
        this.currency     = currency;
        this.balanceAfter = balanceAfter;
        this.eventType    = eventType;
        this.description  = description;
        this.occurredAt   = occurredAt;
    }
}