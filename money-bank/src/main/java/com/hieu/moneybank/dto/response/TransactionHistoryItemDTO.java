package com.hieu.moneybank.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO đại diện cho một mục sao kê tài khoản từ Corebank.
 */
public record TransactionHistoryItemDTO(
        String     id,
        String     accountId,
        String     direction,
        BigDecimal amount,
        String     currency,
        BigDecimal balanceAfter,
        String     eventType,
        String     description,
        Instant    occurredAt
) {}
