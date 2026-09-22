package com.hieu.corebank.dto;

import com.hieu.common.cqrs.Command;
import com.hieu.corebank.dto.AccountResponseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreateRequestDTO implements Command<AccountResponseDTO> {
    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9-]{6,20}", message = "cifNumber must contain 6-20 letters, digits, or hyphens")
    private String cifNumber;

    @NotBlank
    @Pattern(regexp = "[A-Za-z]{3}", message = "currency must be a 3-letter code")
    private String currency;

    @PositiveOrZero
    private BigDecimal initialBalance;
}
