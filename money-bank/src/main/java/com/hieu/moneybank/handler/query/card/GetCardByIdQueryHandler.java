package com.hieu.moneybank.handler.query.card;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.response.IssueCardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetCardByIdQueryHandler implements QueryHandler<GetCardByIdQueryHandler.GetCardByIdQuery, IssueCardResponseDTO> {

    public record GetCardByIdQuery(String cardId) implements Query<IssueCardResponseDTO> {
    }

    private final CorebankClient corebankClient;

    @Override
    public IssueCardResponseDTO handle(GetCardByIdQuery query) {
        return corebankClient.getCard(query.cardId());
    }
}
