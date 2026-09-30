package com.hieu.moneybank.dto.response;

import com.hieu.moneybank.constant.ProposalStatus;
import com.hieu.moneybank.domain.TransferProposal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response cho Transfer Proposal — trả ra ở cả bước tạo và bước xác nhận.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferProposalResponseDTO {

    private String proposalId;
    private ProposalStatus status;

    /** Null cho đến khi CONFIRMED. */
    private String corebankTransactionId;

    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;
    private String currency;
    private String description;

    /** Có giá trị khi FAILED hoặc REJECTED. Null khi PENDING/CONFIRMED. */
    private String failureReason;

    private Instant createdAt;
    private Instant updatedAt;

    public static TransferProposalResponseDTO from(TransferProposal p) {
        return TransferProposalResponseDTO.builder()
            .proposalId(p.getId())
            .status(p.getStatus())
            .corebankTransactionId(p.getCorebankTransactionId())
            .sourceAccountId(p.getSourceAccountId())
            .destinationAccountId(p.getDestinationAccountId())
            .amount(p.getAmount())
            .currency(p.getCurrency())
            .description(p.getDescription())
            .failureReason(p.getFailureReason())
            .createdAt(p.getCreatedAt())
            .updatedAt(p.getUpdatedAt())
            .build();
    }
}
