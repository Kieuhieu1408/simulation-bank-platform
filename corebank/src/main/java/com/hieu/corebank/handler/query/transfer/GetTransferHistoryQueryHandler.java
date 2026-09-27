package com.hieu.corebank.handler.query.transfer;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.dto.TransactionHistoryItemDTO;
import com.hieu.corebank.exception.BusinessException;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.projection.AccountViewRepository;
import com.hieu.corebank.projection.TransactionHistoryViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * V2: Đọc từ transaction_history_view (Read Side).
 * Trả về TransactionHistoryItemDTO với đầy đủ direction và balance_after —
 * thông tin mà V1 không có.
 */
@Component
@RequiredArgsConstructor
public class GetTransferHistoryQueryHandler
        implements QueryHandler<GetTransferHistoryQueryHandler.GetTransferHistoryQuery,
                                Page<TransactionHistoryItemDTO>> {

    public record GetTransferHistoryQuery(
            String accountId,
            Instant from,
            Instant to,
            Pageable pageable
    ) implements Query<Page<TransactionHistoryItemDTO>> {}

    private final AccountViewRepository             accountViews;
    private final TransactionHistoryViewRepository  historyViews;

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionHistoryItemDTO> handle(GetTransferHistoryQuery query) {
        if (!accountViews.existsById(query.accountId())) {
            throw new NotFoundException("Account not found: " + query.accountId());
        }

        Instant start = query.from() == null ? Instant.EPOCH : query.from();
        Instant end   = query.to()   == null ? Instant.now() : query.to();

        if (start.isAfter(end)) {
            throw new BusinessException("from must be before to");
        }

        return historyViews
                .findByAccountId(query.accountId(), start, end, query.pageable())
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