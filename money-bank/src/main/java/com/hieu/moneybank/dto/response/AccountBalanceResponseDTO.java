package com.hieu.moneybank.dto.response;

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
    private String     accountId;
    private String     currency;
    private BigDecimal actualBalance;
    private BigDecimal availableBalance;

    public AccountBalanceResponseDTO(String accountId, String currency, BigDecimal balance) {
        this.accountId = accountId;
        this.currency = currency;
        this.actualBalance = balance;
        this.availableBalance = balance;
    }

    public BigDecimal getBalance() {
        return availableBalance != null ? availableBalance : actualBalance;
    }
}
