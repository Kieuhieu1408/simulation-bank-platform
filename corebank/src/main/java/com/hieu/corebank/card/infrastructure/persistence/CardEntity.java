package com.hieu.corebank.card.infrastructure.persistence;

import com.hieu.common.constant.CardStatus;
import com.hieu.corebank.card.domain.BankCard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bank_cards", uniqueConstraints = @UniqueConstraint(name = "uk_card_number", columnNames = "card_number"))
public class CardEntity {

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

    public static CardEntity fromDomain(BankCard card) {
        CardEntity entity = new CardEntity();
        entity.setId(card.getId());
        entity.setCardNumber(card.getCardNumber());
        entity.setAccountId(card.getAccountId());
        entity.setStatus(card.getStatus());
        entity.setCreatedAt(card.getCreatedAt() != null ? card.getCreatedAt() : Instant.now());
        return entity;
    }

    public BankCard toDomain() {
        return new BankCard(
                this.id,
                this.cardNumber,
                this.accountId,
                this.status,
                this.createdAt
        );
    }
}
