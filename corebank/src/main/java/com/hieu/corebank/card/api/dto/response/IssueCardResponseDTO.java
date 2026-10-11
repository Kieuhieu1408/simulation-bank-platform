package com.hieu.corebank.card.api.dto.response;

import com.hieu.common.constant.CardStatus;
import com.hieu.corebank.card.domain.BankCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * IssueCardResponseDTO — DTO phản hồi thông tin thẻ.
 * Masking số thẻ dạng 4111-22XX-XXXX-3344 theo D13, AC-CB-11.
 * Tuyệt đối không chứa CVV / PIN (D13).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueCardResponseDTO {
    private String cardId;
    private String maskedCardNumber;
    private String accountId;
    private String cifNumber;
    private CardStatus status;
    private Instant createdAt;

    public static IssueCardResponseDTO from(BankCard card) {
        return from(card, null);
    }

    public static IssueCardResponseDTO from(BankCard card, String cifNumber) {
        return IssueCardResponseDTO.builder()
                .cardId(card.getId())
                .maskedCardNumber(card.mask())
                .accountId(card.getAccountId())
                .cifNumber(cifNumber)
                .status(card.getStatus())
                .createdAt(card.getCreatedAt())
                .build();
    }
}
