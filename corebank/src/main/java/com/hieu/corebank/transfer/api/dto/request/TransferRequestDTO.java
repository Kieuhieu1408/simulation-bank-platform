package com.hieu.corebank.transfer.api.dto.request;

import com.hieu.common.cqrs.Command;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequestDTO implements Command<TransferResponseDTO> {
    @NotBlank
    private String sourceAccountId;

    @NotBlank
    private String destinationAccountId;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 17, fraction = 2)
    private BigDecimal amount;

    @NotBlank
    @Pattern(regexp = "[A-Za-z]{3}", message = "currency must be a 3-letter code")
    private String currency;

    @NotBlank
    @Size(max = 100)
    private String idempotencyKey;

    @Size(max = 255)
    private String description;
}
