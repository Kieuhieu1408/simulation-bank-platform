package com.hieu.corebank.customer.api.dto.response;

import com.hieu.corebank.customer.domain.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * CustomerResponseDTO — DTO phản hồi thông tin khách hàng.
 * Che mờ CCCD dạng 012345****01 tuân thủ Nghị định 13/2023/NĐ-CP (AC-CB-11, D13).
 */
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
                .cifNumber(customer.cifNumber())
                .nationalId(customer.maskedNationalId())
                .fullName(customer.fullName())
                .phoneNumber(customer.phoneNumber())
                .email(customer.email())
                .createdAt(customer.createdAt())
                .build();
    }
}
