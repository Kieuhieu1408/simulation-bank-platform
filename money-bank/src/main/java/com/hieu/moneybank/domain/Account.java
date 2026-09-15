package com.hieu.moneybank.domain;

import com.hieu.moneybank.constant.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "accounts", uniqueConstraints = @UniqueConstraint(name = "uk_account_number", columnNames = "account_number"))
public class Account {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cif_number", nullable = false)
    private Customer customer;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Account() {}

    public Account(String accountNumber, Customer customer, String currency, BigDecimal balance) {
        this.id = UUID.randomUUID().toString();
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.currency = currency;
        this.balance = balance;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    public void debit(BigDecimal amount) { balance = balance.subtract(amount); }
    public void credit(BigDecimal amount) { balance = balance.add(amount); }
}
