package com.hieu.corebank.transfer.api.dto.response;

import com.hieu.common.constant.TransactionStatus;
import com.hieu.corebank.transfer.application.port.TransferQueryPort;
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

    public static TransferResponseDTO from(TransferQueryPort.TransferReadModel view) {
        TransactionStatus status;
        String rawStatus = view.status();
        if ("COMPLETED".equalsIgnoreCase(rawStatus) || "SUCCESS".equalsIgnoreCase(rawStatus)) {
            status = TransactionStatus.SUCCESS;
        } else if ("FAILED".equalsIgnoreCase(rawStatus)) {
            status = TransactionStatus.FAILED;
        } else {
            status = TransactionStatus.PENDING;
        }
        return TransferResponseDTO.builder()
                .transactionId(view.transferId())
                .status(status)
                .sourceAccountId(view.sourceAccountId())
                .destinationAccountId(view.destinationAccountId())
                .amount(view.amount())
                .currency(view.currency())
                .description(view.description())
                .createdAt(view.createdAt())
                .build();
    }
}
