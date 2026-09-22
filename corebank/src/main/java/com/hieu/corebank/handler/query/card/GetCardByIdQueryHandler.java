package com.hieu.corebank.handler.query.card;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.domain.BankCard;
import com.hieu.corebank.dto.IssueCardResponseDTO;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetCardByIdQueryHandler implements QueryHandler<GetCardByIdQueryHandler.GetCardByIdQuery, IssueCardResponseDTO> {

    public record GetCardByIdQuery(String cardId) implements Query<IssueCardResponseDTO> {
    }

    private final CardRepository cards;

    @Override
    @Transactional(readOnly = true)
    public IssueCardResponseDTO handle(GetCardByIdQuery query) {
        BankCard card = cards.findById(query.cardId())
                .orElseThrow(() -> new NotFoundException("Card not found: " + query.cardId()));
        return IssueCardResponseDTO.from(card);
    }
}
