package com.hieu.corebank.transfer.api.dto.request;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferGetRequestDTO implements Query<TransferResponseDTO> {
    @NotBlank
    private String transferId;
}
