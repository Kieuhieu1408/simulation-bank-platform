package com.hieu.corebank.account.application.query;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.account.api.dto.response.TransactionHistoryItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public record GetTransactionHistoryQuery(
        String accountId,
        Instant from,
        Instant to,
        Pageable pageable
) implements Query<Page<TransactionHistoryItemDTO>> {
}
