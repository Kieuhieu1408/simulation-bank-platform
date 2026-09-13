package com.hieu.corebank.domain;

import com.hieu.corebank.constant.TransactionStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
    name = "bank_transactions",
    uniqueConstraints = @UniqueConstraint(name = "uk_idempotency_key", columnNames = "idempotency_key"),
    indexes = {
        @Index(name = "idx_tx_source",      columnList = "source_account_id,created_at"),
        @Index(name = "idx_tx_destination", columnList = "destination_account_id,created_at")
    }
)
public class BankTransaction {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "destination_account_id", nullable = false)
    private Account destinationAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected BankTransaction() {}

    public BankTransaction(Account source, Account destination, BigDecimal amount,
                           String currency, String key, String description) {
        this.id = UUID.randomUUID().toString();
        this.sourceAccount = source;
        this.destinationAccount = destination;
        this.amount = amount;
        this.currency = currency;
        this.idempotencyKey = key;
        this.description = description;
        this.status = TransactionStatus.SUCCESS;
        this.createdAt = Instant.now();
    }
}
