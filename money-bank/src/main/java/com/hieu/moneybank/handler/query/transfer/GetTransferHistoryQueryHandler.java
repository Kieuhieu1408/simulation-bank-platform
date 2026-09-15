package com.hieu.moneybank.handler.query.transfer;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.moneybank.domain.BankTransaction;
import com.hieu.moneybank.dto.response.TransferResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.exception.NotFoundException;
import com.hieu.moneybank.repository.AccountRepository;
import com.hieu.moneybank.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class GetTransferHistoryQueryHandler implements QueryHandler<GetTransferHistoryQueryHandler.GetTransferHistoryQuery, Page<TransferResponseDTO>> {

    public record GetTransferHistoryQuery(
            String accountId,
            Instant from,
            Instant to,
            Pageable pageable
    ) implements Query<Page<TransferResponseDTO>> {
    }

    private final AccountRepository accounts;
    private final TransactionRepository transactions;

    public GetTransferHistoryQueryHandler(AccountRepository accounts, TransactionRepository transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferResponseDTO> handle(GetTransferHistoryQuery query) {
        if (!accounts.existsById(query.accountId())) {
            throw new NotFoundException("Account not found: " + query.accountId());
        }

        Instant start = query.from() == null ? Instant.EPOCH : query.from();
        Instant end = query.to() == null ? Instant.now() : query.to();

        if (start.isAfter(end)) {
            throw new BusinessException("from must be before to");
        }

        Page<BankTransaction> page = transactions.findHistory(query.accountId(), start, end, query.pageable());
        return page.map(TransferResponseDTO::from);
    }
}
