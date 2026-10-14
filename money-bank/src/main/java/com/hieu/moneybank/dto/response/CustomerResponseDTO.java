package com.hieu.moneybank.dto.response;

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
}
