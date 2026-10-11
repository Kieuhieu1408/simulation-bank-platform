package com.hieu.corebank.card.domain;

import com.hieu.common.constant.CardStatus;
import com.hieu.corebank.shared.domain.CardNumber;
import com.hieu.corebank.shared.domain.DomainException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * BankCard — Domain Entity đại diện cho Thẻ Ngân hàng (100% thuần khiết).
 * Không phụ thuộc Spring, JPA, Jackson.
 */
public class BankCard {

    private final String id;
    private final String cardNumber;
    private final String accountId;
    private CardStatus status;
    private final Instant createdAt;

    public BankCard(String id, String cardNumber, String accountId, CardStatus status, Instant createdAt) {
        if (id == null || id.isBlank()) {
            throw new DomainException("Card ID cannot be null or blank");
        }
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new DomainException("Card number cannot be null or blank");
        }
        if (accountId == null || accountId.isBlank()) {
            throw new DomainException("Account ID cannot be null or blank");
        }
        this.id = id;
        this.cardNumber = cardNumber;
        this.accountId = accountId;
        this.status = status != null ? status : CardStatus.ACTIVE;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static BankCard issue(String cardNumber, String accountId) {
        return new BankCard(UUID.randomUUID().toString(), cardNumber, accountId, CardStatus.ACTIVE, Instant.now());
    }

    public static BankCard issue(String id, String cardNumber, String accountId) {
        return new BankCard(id, cardNumber, accountId, CardStatus.ACTIVE, Instant.now());
    }

    public void block() {
        if (this.status == CardStatus.BLOCKED) {
            return;
        }
        this.status = CardStatus.BLOCKED;
    }

    public void unblock() {
        if (this.status == CardStatus.ACTIVE) {
            return;
        }
        this.status = CardStatus.ACTIVE;
    }

    /**
     * Che mờ PAN thẻ theo chuẩn D13 & PCI DSS: 4111-22XX-XXXX-3344 (AC-CB-11).
     */
    public String mask() {
        return CardNumber.of(this.cardNumber).mask();
    }

    public String getId() {
        return id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getAccountId() {
        return accountId;
    }

    public CardStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankCard bankCard = (BankCard) o;
        return Objects.equals(id, bankCard.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
