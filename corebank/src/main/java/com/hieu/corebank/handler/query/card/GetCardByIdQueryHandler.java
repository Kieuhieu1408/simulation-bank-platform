package com.hieu.corebank.handler.query.card;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.domain.BankCard;
import com.hieu.corebank.dto.response.IssueCardResponseDTO;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.projection.AccountView;
import com.hieu.corebank.projection.AccountViewRepository;
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
    private final AccountViewRepository accountViews;

    @Override
    @Transactional(readOnly = true)
    public IssueCardResponseDTO handle(GetCardByIdQuery query) {
        BankCard card = cards.findById(query.cardId())
                .orElseThrow(() -> new NotFoundException("Card not found: " + query.cardId()));
        String cif = accountViews.findById(card.getAccountId())
                .map(AccountView::getCustomerId)
                .orElse(null);
        return IssueCardResponseDTO.from(card, cif);
    }
}
