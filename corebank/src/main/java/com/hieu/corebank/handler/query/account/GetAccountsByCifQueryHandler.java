package com.hieu.corebank.handler.query.account;

import com.hieu.common.cqrs.Dispatcher;
import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.domain.Account;
import com.hieu.corebank.dto.AccountResponseDTO;
import com.hieu.corebank.handler.query.customer.GetCustomerQueryHandler.GetCustomerQuery;
import com.hieu.corebank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetAccountsByCifQueryHandler implements QueryHandler<GetAccountsByCifQueryHandler.GetAccountsByCifQuery, List<AccountResponseDTO>> {

    public record GetAccountsByCifQuery(String cifNumber) implements Query<List<AccountResponseDTO>> {
    }

    private final AccountRepository accounts;
    private final Dispatcher dispatcher;

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> handle(GetAccountsByCifQuery query) {
        // Ensure customer exists
        dispatcher.dispatch(new GetCustomerQuery(query.cifNumber()));
        List<Account> accountList = accounts.findByCustomerCifNumberOrderByCreatedAtDesc(query.cifNumber().toUpperCase());
        return accountList.stream().map(AccountResponseDTO::from).collect(Collectors.toList());
    }
}
