package com.hieu.corebank.handler.query.account;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.domain.Account;
import com.hieu.corebank.dto.response.AccountBalanceResponseDTO;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetAccountBalanceQueryHandler implements QueryHandler<GetAccountBalanceQueryHandler.GetAccountBalanceQuery, AccountBalanceResponseDTO> {

    public record GetAccountBalanceQuery(String accountId) implements Query<AccountBalanceResponseDTO> {
    }

    private final AccountRepository accounts;

    @Override
    @Transactional(readOnly = true)
    public AccountBalanceResponseDTO handle(GetAccountBalanceQuery query) {
        Account account = accounts.findById(query.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found: " + query.accountId()));
        return new AccountBalanceResponseDTO(account.getId(), account.getCurrency(), account.getBalance());
    }
}
