package com.hieu.corebank.handler.query.card;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.domain.BankCard;
import com.hieu.corebank.dto.response.CardResponseDTO;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.repository.CardRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetCardByIdQueryHandler implements QueryHandler<GetCardByIdQueryHandler.GetCardByIdQuery, CardResponseDTO> {

    public record GetCardByIdQuery(String cardId) implements Query<CardResponseDTO> {
    }

    private final CardRepository cards;

    public GetCardByIdQueryHandler(CardRepository cards) {
        this.cards = cards;
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponseDTO handle(GetCardByIdQuery query) {
        BankCard card = cards.findById(query.cardId())
                .orElseThrow(() -> new NotFoundException("Card not found: " + query.cardId()));
        return CardResponseDTO.from(card);
    }
}
