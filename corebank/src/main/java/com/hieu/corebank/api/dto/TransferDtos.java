package com.hieu.corebank.api.dto;

import com.hieu.corebank.domain.BankTransaction;
import com.hieu.corebank.constant.TransactionStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public final class TransferDtos {
    private TransferDtos() {}
    public record Request(
            @NotBlank String sourceAccountId,
            @NotBlank String destinationAccountId,
            @NotNull @DecimalMin(value = "0.01") @Digits(integer = 17, fraction = 2) BigDecimal amount,
            @NotBlank @Pattern(regexp = "[A-Za-z]{3}", message = "currency must be a 3-letter code") String currency,
            @NotBlank @Size(max = 100) String idempotencyKey,
            @Size(max = 255) String description) {}
    public record Response(String transactionId, TransactionStatus status, String sourceAccountId,
                           String destinationAccountId, BigDecimal amount, String currency,
                           String description, Instant createdAt) {
        public static Response from(BankTransaction t) {
            return new Response(t.getId(), t.getStatus(), t.getSourceAccount().getId(),
                    t.getDestinationAccount().getId(), t.getAmount(), t.getCurrency(),
                    t.getDescription(), t.getCreatedAt());
        }
    }
}
