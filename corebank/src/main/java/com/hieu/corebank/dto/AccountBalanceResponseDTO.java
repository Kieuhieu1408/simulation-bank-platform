package com.hieu.corebank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalanceResponseDTO {
    private String accountId;
    private String currency;
    private BigDecimal balance;
}