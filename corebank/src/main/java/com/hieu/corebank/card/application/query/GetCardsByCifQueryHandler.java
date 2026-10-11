package com.hieu.corebank.card.application.query;

import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.account.infrastructure.projection.AccountView;
import com.hieu.corebank.account.infrastructure.projection.AccountViewRepository;
import com.hieu.corebank.card.api.dto.response.IssueCardResponseDTO;
import com.hieu.corebank.card.domain.BankCard;
import com.hieu.corebank.card.domain.CardRepository;
import com.hieu.corebank.shared.constant.ProfileConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Profile({ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class GetCardsByCifQueryHandler implements QueryHandler<GetCardsByCifQuery, List<IssueCardResponseDTO>> {

    private final CardRepository        cards;
    private final AccountViewRepository accountViews;

    @Override
    @Transactional(readOnly = true)
    public List<IssueCardResponseDTO> handle(GetCardsByCifQuery query) {
        String cif = query.cifNumber().toUpperCase();
        List<AccountView> userAccounts = accountViews.findByCustomerIdOrderByLastUpdatedAtDesc(cif);
        List<String> accountIds = userAccounts.stream().map(AccountView::getAccountId).toList();
        if (accountIds.isEmpty()) {
            return List.of();
        }
        List<BankCard> cardList = cards.findByAccountIdInOrderByCreatedAtDesc(accountIds);
        return cardList.stream().map(card -> IssueCardResponseDTO.from(card, cif)).collect(Collectors.toList());
    }
}
