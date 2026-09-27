package com.hieu.corebank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * V2: Thêm availableBalance để phân biệt Actual vs Available.
 * Breaking change có chủ ý — API v1 chỉ có balance, v2 có 2 trường.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalanceResponseDTO {
    private String     accountId;
    private String     currency;
    private BigDecimal actualBalance;    // Số dư sổ cái
    private BigDecimal availableBalance; // Số dư khả dụng (đã trừ pending)
}