package com.hieu.moneybank.handler.command.card;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.request.IssueCardRequestDTO;
import com.hieu.moneybank.dto.response.IssueCardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IssueCardCommandHandler implements CommandHandler<IssueCardRequestDTO, IssueCardResponseDTO> {

    private final CorebankClient corebankClient;

    @Override
    public IssueCardResponseDTO handle(IssueCardRequestDTO command) {
        return corebankClient.issueCard(command.getAccountId());
    }
}
