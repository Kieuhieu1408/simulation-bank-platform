package com.hieu.corebank.domain;

import com.hieu.common.constant.CardStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "bank_cards", uniqueConstraints = @UniqueConstraint(name = "uk_card_number", columnNames = "card_number"))
public class BankCard {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "card_number", nullable = false, length = 16)
    private String cardNumber;

    @Column(name = "account_id", nullable = false, length = 36)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected BankCard() {}

    public BankCard(String cardNumber, String accountId) {
        this.id = UUID.randomUUID().toString();
        this.cardNumber = cardNumber;
        this.accountId = accountId;
        this.status = CardStatus.ACTIVE;
        this.createdAt = Instant.now();
    }
}
