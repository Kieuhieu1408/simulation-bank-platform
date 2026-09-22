package com.hieu.corebank.handler.query.account;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.domain.Account;
import com.hieu.corebank.dto.AccountResponseDTO;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetAccountByIdQueryHandler implements QueryHandler<GetAccountByIdQueryHandler.GetAccountByIdQuery, AccountResponseDTO> {

    public record GetAccountByIdQuery(String accountId) implements Query<AccountResponseDTO> {
    }

    private final AccountRepository accounts;

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDTO handle(GetAccountByIdQuery query) {
        Account account = accounts.findById(query.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found: " + query.accountId()));
        return AccountResponseDTO.from(account);
    }
}
