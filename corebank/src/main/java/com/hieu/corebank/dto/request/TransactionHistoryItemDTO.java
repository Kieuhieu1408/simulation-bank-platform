package com.hieu.corebank.dto.request;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * V2: Thay thế TransferResponseDTO cho chiều Query lịch sử.
 * Thêm direction (DEBIT/CREDIT) và balanceAfter — đặc trưng của sao kê ngân hàng.
 */
public record TransactionHistoryItemDTO(
        String     id,
        String     accountId,
        String     direction,      // DEBIT | CREDIT
        BigDecimal amount,
        String     currency,
        BigDecimal balanceAfter,   // Số dư khả dụng sau giao dịch
        String     eventType,
        String     description,
        Instant    occurredAt
) {}