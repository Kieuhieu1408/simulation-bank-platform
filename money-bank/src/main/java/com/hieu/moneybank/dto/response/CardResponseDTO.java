package com.hieu.moneybank.dto.response;

import com.hieu.moneybank.constant.CardStatus;
import com.hieu.moneybank.domain.BankCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponseDTO {
    private String cardId;
    private String maskedCardNumber;
    private String cvv;
    private String accountId;
    private String cifNumber;
    private CardStatus status;
    private Instant createdAt;

    public static CardResponseDTO from(BankCard card) {
        String number = card.getCardNumber();
        String masked = number.substring(0, 4) + " **** **** " + number.substring(12);
        return CardResponseDTO.builder()
                .cardId(card.getId())
                .maskedCardNumber(masked)
                .cvv(card.getCvv())
                .accountId(card.getAccount().getId())
                .cifNumber(card.getAccount().getCustomer().getCifNumber())
                .status(card.getStatus())
                .createdAt(card.getCreatedAt())
                .build();
    }
}