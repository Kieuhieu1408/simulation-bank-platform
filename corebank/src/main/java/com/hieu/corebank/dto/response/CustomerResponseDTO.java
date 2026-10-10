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
    private String nationalId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private Instant createdAt;

    public static CustomerResponseDTO from(Customer customer) {
        return CustomerResponseDTO.builder()
                .cifNumber(customer.getCifNumber())
                .nationalId(customer.getNationalId())
                .fullName(customer.getFullName())
                .phoneNumber(customer.getPhoneNumber())
                .email(customer.getEmail())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}