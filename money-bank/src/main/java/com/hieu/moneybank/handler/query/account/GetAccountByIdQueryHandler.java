package com.hieu.moneybank.handler.query.account;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.response.AccountResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAccountByIdQueryHandler implements QueryHandler<GetAccountByIdQueryHandler.GetAccountByIdQueryRequestDTO, AccountResponseDTO> {

    public record GetAccountByIdQueryRequestDTO(String accountId) implements Query<AccountResponseDTO> {
    }

    private final CorebankClient corebankClient;

    @Override
    public AccountResponseDTO handle(GetAccountByIdQueryRequestDTO query) {
        return corebankClient.getAccount(query.accountId());
    }
}
