package com.hieu.corebank.dto.request;

import com.hieu.common.cqrs.Command;
import com.hieu.corebank.dto.response.CustomerResponseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerCreateRequestDTO implements Command<CustomerResponseDTO> {
    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9-]{6,20}", message = "cifNumber must contain 6-20 letters, digits, or hyphens")
    private String cifNumber;
}