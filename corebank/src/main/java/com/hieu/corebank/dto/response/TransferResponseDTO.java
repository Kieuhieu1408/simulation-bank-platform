package com.hieu.corebank.dto.response;

import com.hieu.common.constant.TransactionStatus;
import com.hieu.corebank.projection.TransferView;
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

    public static TransferResponseDTO from(TransferView view) {
        return TransferResponseDTO.builder()
                .transactionId(view.getTransferId())
                .status(TransactionStatus.valueOf(view.getStatus()))
                .sourceAccountId(view.getSourceAccountId())
                .destinationAccountId(view.getDestinationAccountId())
                .amount(view.getAmount())
                .currency(view.getCurrency())
                .description(view.getDescription())
                .createdAt(view.getCreatedAt())
                .build();
    }
}