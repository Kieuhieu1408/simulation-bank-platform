package com.hieu.corebank.customer.api.dto.request;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.customer.api.dto.response.CustomerResponseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerGetRequestDTO implements Query<CustomerResponseDTO> {
    @NotBlank
    private String cifNumber;
}
