package com.hieu.moneybank.handler.query.account;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.moneybank.domain.Account;
import com.hieu.moneybank.dto.response.AccountResponseDTO;
import com.hieu.moneybank.exception.NotFoundException;
import com.hieu.moneybank.repository.AccountRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetAccountByIdQueryHandler implements QueryHandler<GetAccountByIdQueryHandler.GetAccountByIdQueryRequestDTO, AccountResponseDTO> {

    public record GetAccountByIdQueryRequestDTO(String accountId) implements Query<AccountResponseDTO> {
    }

    private final AccountRepository accounts;

    public GetAccountByIdQueryHandler(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDTO handle(GetAccountByIdQueryRequestDTO query) {
        Account account = accounts.findById(query.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found: " + query.accountId()));
        return AccountResponseDTO.from(account);
    }
}
