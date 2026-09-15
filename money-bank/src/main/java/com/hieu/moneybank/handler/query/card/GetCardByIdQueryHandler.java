package com.hieu.moneybank.handler.query.card;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.moneybank.domain.BankCard;
import com.hieu.moneybank.dto.response.CardResponseDTO;
import com.hieu.moneybank.exception.NotFoundException;
import com.hieu.moneybank.repository.CardRepository;
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
