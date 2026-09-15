package com.hieu.moneybank.dto.response;

import com.hieu.moneybank.constant.TransactionStatus;
import com.hieu.moneybank.domain.BankTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponseDTO {
    private String transactionId;
    private TransactionStatus status;
    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private Instant createdAt;

    public static TransferResponseDTO from(BankTransaction t) {
        return TransferResponseDTO.builder()
                .transactionId(t.getId())
                .status(t.getStatus())
                .sourceAccountId(t.getSourceAccount().getId())
                .destinationAccountId(t.getDestinationAccount().getId())
                .amount(t.getAmount())
                .currency(t.getCurrency())
                .description(t.getDescription())
                .createdAt(t.getCreatedAt())
                .build();
    }
}