package com.hieu.moneybank.handler.query.card;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.response.IssueCardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetCardsByCifQueryHandler implements QueryHandler<GetCardsByCifQueryHandler.GetCardsByCifQuery, List<IssueCardResponseDTO>> {

    public record GetCardsByCifQuery(String cifNumber) implements Query<List<IssueCardResponseDTO>> {
    }

    private final CorebankClient corebankClient;

    @Override
    public List<IssueCardResponseDTO> handle(GetCardsByCifQuery query) {
        return corebankClient.getCardsByCif(query.cifNumber().toUpperCase());
    }
}
