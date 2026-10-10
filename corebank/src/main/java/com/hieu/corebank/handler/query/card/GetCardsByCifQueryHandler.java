package com.hieu.corebank.handler.query.card;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.domain.BankCard;
import com.hieu.corebank.dto.response.IssueCardResponseDTO;
import com.hieu.corebank.projection.AccountView;
import com.hieu.corebank.projection.AccountViewRepository;
import com.hieu.corebank.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetCardsByCifQueryHandler implements QueryHandler<GetCardsByCifQueryHandler.GetCardsByCifQuery, List<IssueCardResponseDTO>> {

    public record GetCardsByCifQuery(String cifNumber) implements Query<List<IssueCardResponseDTO>> {
    }

    private final CardRepository cards;
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
