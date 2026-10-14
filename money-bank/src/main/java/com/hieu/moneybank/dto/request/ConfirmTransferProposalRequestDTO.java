package com.hieu.moneybank.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmTransferProposalRequestDTO {
    /**
     * Mã OTP xác thực giao dịch (mặc định cho test: 111111).
     */
    private String otp;
}
