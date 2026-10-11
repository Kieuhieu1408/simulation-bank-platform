package com.hieu.corebank.transfer.api.dto.response;

import com.hieu.common.constant.TransactionStatus;
import com.hieu.corebank.transfer.infrastructure.projection.TransferView;
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
        TransactionStatus status;
        String rawStatus = view.getStatus();
        if ("COMPLETED".equalsIgnoreCase(rawStatus) || "SUCCESS".equalsIgnoreCase(rawStatus)) {
            status = TransactionStatus.SUCCESS;
        } else if ("FAILED".equalsIgnoreCase(rawStatus)) {
            status = TransactionStatus.FAILED;
        } else {
            status = TransactionStatus.PENDING;
        }
        return TransferResponseDTO.builder()
                .transactionId(view.getTransferId())
                .status(status)
                .sourceAccountId(view.getSourceAccountId())
                .destinationAccountId(view.getDestinationAccountId())
                .amount(view.getAmount())
                .currency(view.getCurrency())
                .description(view.getDescription())
                .createdAt(view.getCreatedAt())
                .build();
    }
}
