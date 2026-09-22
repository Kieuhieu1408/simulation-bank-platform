package com.hieu.corebank.dto;

import com.hieu.corebank.constant.CardStatus;
import com.hieu.corebank.domain.BankCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueCardResponseDTO {
    private String cardId;
    private String maskedCardNumber;
    private String cvv;
    private String accountId;
    private String cifNumber;
    private CardStatus status;
    private Instant createdAt;

    public static IssueCardResponseDTO from(BankCard card) {
        String number = card.getCardNumber();
        String masked = number.substring(0, 4) + " **** **** " + number.substring(12);
        return IssueCardResponseDTO.builder()
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