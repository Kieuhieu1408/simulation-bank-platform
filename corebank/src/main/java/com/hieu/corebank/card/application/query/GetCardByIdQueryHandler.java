package com.hieu.corebank.card.application.query;

import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.account.infrastructure.projection.AccountView;
import com.hieu.corebank.account.infrastructure.projection.AccountViewRepository;
import com.hieu.corebank.card.api.dto.request.CardGetRequestDTO;
import com.hieu.corebank.card.api.dto.response.IssueCardResponseDTO;
import com.hieu.corebank.card.domain.BankCard;
import com.hieu.corebank.card.domain.CardRepository;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile({ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class GetCardByIdQueryHandler implements QueryHandler<CardGetRequestDTO, IssueCardResponseDTO> {

    private final CardRepository        cards;
    private final AccountViewRepository accountViews;

    @Override
    @Transactional(readOnly = true)
    public IssueCardResponseDTO handle(CardGetRequestDTO query) {
        BankCard card = cards.findById(query.getCardId())
                .orElseThrow(() -> new NotFoundException("Card not found: " + query.getCardId()));
        String cif = accountViews.findById(card.getAccountId())
                .map(AccountView::getCustomerId)
                .orElse(null);
        return IssueCardResponseDTO.from(card, cif);
    }
}
