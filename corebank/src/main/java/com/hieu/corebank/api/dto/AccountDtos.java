package com.hieu.corebank.api.dto;

import com.hieu.corebank.domain.Account;
import com.hieu.corebank.constant.AccountStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;

public final class AccountDtos {
    private AccountDtos() {}
    public record CreateRequest(
            @NotBlank @Pattern(regexp = "[A-Za-z0-9-]{6,20}", message = "cifNumber must contain 6-20 letters, digits, or hyphens") String cifNumber,
            @NotBlank @Pattern(regexp = "[A-Za-z]{3}", message = "currency must be a 3-letter code") String currency,
            @PositiveOrZero BigDecimal initialBalance) {}
    public record Response(String accountId, String accountNumber, String cifNumber, String currency,
                           BigDecimal balance, AccountStatus status, Instant createdAt) {
        public static Response from(Account a) {
            return new Response(a.getId(), a.getAccountNumber(), a.getCustomer().getCifNumber(), a.getCurrency(),
                    a.getBalance(), a.getStatus(), a.getCreatedAt());
        }
    }
    public record BalanceResponse(String accountId, String currency, BigDecimal balance) {}
}
