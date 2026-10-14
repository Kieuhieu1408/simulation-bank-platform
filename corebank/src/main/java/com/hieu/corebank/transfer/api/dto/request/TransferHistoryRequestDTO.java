package com.hieu.corebank.transfer.api.dto.request;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferHistoryRequestDTO implements Query<List<TransferResponseDTO>> {
    @NotBlank
    private String accountId;
}
