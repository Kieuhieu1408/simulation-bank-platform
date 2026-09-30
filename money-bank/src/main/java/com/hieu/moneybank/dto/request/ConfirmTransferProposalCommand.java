package com.hieu.moneybank.dto.request;

import com.hieu.common.annotation.ValidCommand;
import com.hieu.common.cqrs.Command;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command xác nhận Transfer Proposal (U-06 — bước 2/2).
 *
 * <p>Trigger luồng: PENDING → VALIDATING → CONFIRMED | FAILED.
 * Corebank được gọi trong bước này.
 */
@ValidCommand
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmTransferProposalCommand implements Command<TransferProposalResponseDTO> {

    @NotBlank(message = "proposalId không được để trống")
    private String proposalId;

    /**
     * CIF của người xác nhận — inject từ identity context, không từ body.
     */
    private String confirmerCustomerId;
}
