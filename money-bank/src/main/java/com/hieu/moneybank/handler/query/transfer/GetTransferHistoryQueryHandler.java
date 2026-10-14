package com.hieu.moneybank.handler.query.transfer;

import com.hieu.common.cqrs.QueryHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.request.TransactionHistoryRequestDTO;
import com.hieu.moneybank.dto.response.TransactionHistoryItemDTO;
import com.hieu.moneybank.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class GetTransferHistoryQueryHandler implements QueryHandler<TransactionHistoryRequestDTO, Page<TransactionHistoryItemDTO>> {

    private final CorebankClient corebankClient;

    @Override
    public Page<TransactionHistoryItemDTO> handle(TransactionHistoryRequestDTO query) {
        Instant start = query.getFrom() == null ? Instant.EPOCH : query.getFrom();
        Instant end = query.getTo() == null ? Instant.now() : query.getTo();

        if (start.isAfter(end)) {
            throw new BusinessException("from must be before to");
        }

        int page = query.getPageable() != null ? query.getPageable().getPageNumber() : 0;
        int size = query.getPageable() != null ? query.getPageable().getPageSize() : 20;

        return corebankClient.getTransactionHistory(query.getAccountId(), query.getFrom(), query.getTo(), page, size);
    }
}
