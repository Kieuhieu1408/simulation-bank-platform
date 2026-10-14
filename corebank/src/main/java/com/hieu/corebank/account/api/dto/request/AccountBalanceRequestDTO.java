package com.hieu.corebank.account.api.dto.request;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.account.api.dto.response.AccountBalanceResponseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalanceRequestDTO implements Query<AccountBalanceResponseDTO> {
    @NotBlank
    private String accountId;
}
