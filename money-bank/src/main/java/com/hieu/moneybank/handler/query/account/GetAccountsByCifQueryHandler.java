package com.hieu.moneybank.handler.query.account;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.response.AccountResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAccountsByCifQueryHandler implements QueryHandler<GetAccountsByCifQueryHandler.GetAccountsByCifQuery, List<AccountResponseDTO>> {

    public record GetAccountsByCifQuery(String cifNumber) implements Query<List<AccountResponseDTO>> {
    }

    private final CorebankClient corebankClient;

    @Override
    public List<AccountResponseDTO> handle(GetAccountsByCifQuery query) {
        return corebankClient.getAccountsByCif(query.cifNumber().toUpperCase());
    }
}
