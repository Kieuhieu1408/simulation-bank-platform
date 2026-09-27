package com.hieu.corebank.handler.query.account;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.dto.AccountBalanceResponseDTO;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.projection.AccountView;
import com.hieu.corebank.projection.AccountViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * V2: Đọc từ account_view (Read Side) thay vì bảng accounts (Write Side).
 * Tốc độ SELECT trên read-only table, không tranh chấp với Command flow.
 */
@Component
@RequiredArgsConstructor
public class GetAccountBalanceQueryHandler
        implements QueryHandler<GetAccountBalanceQueryHandler.GetAccountBalanceQuery, AccountBalanceResponseDTO> {

    public record GetAccountBalanceQuery(String accountId)
            implements Query<AccountBalanceResponseDTO> {}

    private final AccountViewRepository accountViews;

    @Override
    @Transactional(readOnly = true)
    public AccountBalanceResponseDTO handle(GetAccountBalanceQuery query) {
        AccountView view = accountViews.findById(query.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found: " + query.accountId()));

        return AccountBalanceResponseDTO.builder()
                .accountId(view.getAccountId())
                .currency(view.getCurrency())
                .actualBalance(view.getActualBalance())
                .availableBalance(view.getAvailableBalance())
                .build();
    }
}