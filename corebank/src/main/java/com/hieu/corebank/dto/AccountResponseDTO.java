package com.hieu.corebank.dto;

import com.hieu.corebank.constant.AccountStatus;
import com.hieu.corebank.domain.Account;
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

    public static AccountResponseDTO from(Account a) {
        return AccountResponseDTO.builder()
                .accountId(a.getId())
                .accountNumber(a.getAccountNumber())
                .cifNumber(a.getCustomer().getCifNumber())
                .currency(a.getCurrency())
                .balance(a.getBalance())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .build();
    }
}