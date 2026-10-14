package com.hieu.moneybank.handler.query.transfer;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.response.TransactionHistoryItemDTO;
import com.hieu.moneybank.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class GetTransferHistoryQueryHandler implements QueryHandler<GetTransferHistoryQueryHandler.GetTransferHistoryQuery, Page<TransactionHistoryItemDTO>> {

    public record GetTransferHistoryQuery(
            String accountId,
            Instant from,
            Instant to,
            Pageable pageable
    ) implements Query<Page<TransactionHistoryItemDTO>> {
    }

    private final CorebankClient corebankClient;

    @Override
    public Page<TransactionHistoryItemDTO> handle(GetTransferHistoryQuery query) {
        Instant start = query.from() == null ? Instant.EPOCH : query.from();
        Instant end = query.to() == null ? Instant.now() : query.to();

        if (start.isAfter(end)) {
            throw new BusinessException("from must be before to");
        }

        int page = query.pageable() != null ? query.pageable().getPageNumber() : 0;
        int size = query.pageable() != null ? query.pageable().getPageSize() : 20;

        return corebankClient.getTransactionHistory(query.accountId(), query.from(), query.to(), page, size);
    }
}
