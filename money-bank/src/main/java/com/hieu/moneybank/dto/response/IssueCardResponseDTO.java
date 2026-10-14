package com.hieu.moneybank.dto.response;

import com.hieu.common.constant.CardStatus;
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
}
