package com.hieu.corebank.account.api.dto.request;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
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
public class AccountsByCifRequestDTO implements Query<List<AccountResponseDTO>> {
    @NotBlank
    private String cifNumber;
}
