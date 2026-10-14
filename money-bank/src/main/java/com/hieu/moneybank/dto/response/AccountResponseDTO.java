package com.hieu.moneybank.dto.response;

import com.hieu.common.constant.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDTO {
    private String accountId;
    private String accountNumber;
    private String cifNumber;
    private String currency;
    private BigDecimal balance;
    private AccountStatus status;
    private Instant createdAt;
}
