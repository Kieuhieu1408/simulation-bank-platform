package com.hieu.corebank.dto.response;

import com.hieu.corebank.domain.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDTO {
    private String cifNumber;
    private Instant createdAt;

    public static CustomerResponseDTO from(Customer customer) {
        return CustomerResponseDTO.builder()
                .cifNumber(customer.getCifNumber())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}