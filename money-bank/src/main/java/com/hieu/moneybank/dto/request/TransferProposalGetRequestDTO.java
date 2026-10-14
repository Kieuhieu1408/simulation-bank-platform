package com.hieu.moneybank.dto.request;

import com.hieu.common.cqrs.Query;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferProposalGetRequestDTO implements Query<TransferProposalResponseDTO> {
    @NotBlank
    private String proposalId;
}
