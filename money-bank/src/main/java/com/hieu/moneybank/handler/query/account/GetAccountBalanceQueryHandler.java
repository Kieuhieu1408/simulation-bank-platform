package com.hieu.moneybank.handler.query.account;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.response.AccountBalanceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAccountBalanceQueryHandler implements QueryHandler<GetAccountBalanceQueryHandler.GetAccountBalanceQuery, AccountBalanceResponseDTO> {

    public record GetAccountBalanceQuery(String accountId) implements Query<AccountBalanceResponseDTO> {
    }

    private final CorebankClient corebankClient;

    @Override
    public AccountBalanceResponseDTO handle(GetAccountBalanceQuery query) {
        return corebankClient.getAccountBalance(query.accountId());
    }
}
