package com.hieu.corebank.handler.query.card;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.domain.BankCard;
import com.hieu.corebank.dto.IssueCardResponseDTO;
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

    @Override
    @Transactional(readOnly = true)
    public List<IssueCardResponseDTO> handle(GetCardsByCifQuery query) {
        List<BankCard> cardList = cards.findByAccountCustomerCifNumberOrderByCreatedAtDesc(query.cifNumber().toUpperCase());
        return cardList.stream().map(IssueCardResponseDTO::from).collect(Collectors.toList());
    }
}
