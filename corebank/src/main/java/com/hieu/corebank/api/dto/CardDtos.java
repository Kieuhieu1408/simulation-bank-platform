package com.hieu.corebank.api.dto;

import com.hieu.corebank.domain.BankCard;
import com.hieu.corebank.constant.CardStatus;
import java.time.Instant;

public final class CardDtos {
    private CardDtos() {}
    public record Response(String cardId, String maskedCardNumber, String cvv, String accountId, String cifNumber,
                           CardStatus status, Instant createdAt) {
        public static Response from(BankCard card) {
            String number = card.getCardNumber();
            String masked = number.substring(0, 4) + " **** **** " + number.substring(12);
            return new Response(card.getId(), masked, card.getCvv(), card.getAccount().getId(), card.getAccount().getCustomer().getCifNumber(),
                    card.getStatus(), card.getCreatedAt());
        }
    }
}
