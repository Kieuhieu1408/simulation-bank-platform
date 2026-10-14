package com.hieu.moneybank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpResponseDTO {
    private String proposalId;
    private String otp;
    private String message;
    private long expiresInSeconds;
}
