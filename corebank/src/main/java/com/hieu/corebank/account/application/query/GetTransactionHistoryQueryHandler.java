package com.hieu.corebank.account.application.query;

import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.account.api.dto.request.TransactionHistoryRequestDTO;
import com.hieu.corebank.account.api.dto.response.TransactionHistoryItemDTO;
import com.hieu.corebank.account.infrastructure.projection.AccountViewRepository;
import com.hieu.corebank.account.infrastructure.projection.TransactionHistoryViewRepository;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.BusinessException;
import com.hieu.corebank.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * GetTransactionHistoryQueryHandler — Đọc lịch sử giao dịch từ transaction_history_view (Read Side).
 */
@Profile({ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class GetTransactionHistoryQueryHandler
        implements QueryHandler<TransactionHistoryRequestDTO, Page<TransactionHistoryItemDTO>> {

    private final AccountViewRepository             accountViews;
    private final TransactionHistoryViewRepository  historyViews;

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionHistoryItemDTO> handle(TransactionHistoryRequestDTO query) {
        if (!accountViews.existsById(query.getAccountId())) {
            throw new NotFoundException("Account not found: " + query.getAccountId());
        }

        Instant start = query.getFrom() == null ? Instant.EPOCH : query.getFrom();
        Instant end   = query.getTo()   == null ? Instant.now() : query.getTo();

        if (start.isAfter(end)) {
            throw new BusinessException("from must be before to");
        }

        return historyViews
                .findByAccountId(query.getAccountId(), start, end, query.getPageable())
                .map(row -> new TransactionHistoryItemDTO(
                        row.getId(),
                        row.getAccountId(),
                        row.getDirection(),
                        row.getAmount(),
                        row.getCurrency(),
                        row.getBalanceAfter(),
                        row.getEventType(),
                        row.getDescription(),
                        row.getOccurredAt()
                ));
    }
}
