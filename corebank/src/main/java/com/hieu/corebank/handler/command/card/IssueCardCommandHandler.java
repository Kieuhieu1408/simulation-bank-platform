package com.hieu.corebank.handler.command.card;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.domain.BankCard;
import com.hieu.corebank.dto.request.IssueCardRequestDTO;
import com.hieu.corebank.dto.response.IssueCardResponseDTO;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.projection.AccountView;
import com.hieu.corebank.projection.AccountViewRepository;
import com.hieu.corebank.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class IssueCardCommandHandler implements CommandHandler<IssueCardRequestDTO, IssueCardResponseDTO> {

    private final CardRepository cards;
    private final AccountViewRepository accounts;
    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public IssueCardResponseDTO handle(IssueCardRequestDTO command) {
        AccountView account = accounts.findById(command.getAccountId())
                .orElseThrow(() -> new NotFoundException("Account not found: " + command.getAccountId()));
        BankCard card = cards.save(new BankCard(
                nextCardNumber(),
                account.getAccountId()
        ));
        return IssueCardResponseDTO.from(card, account.getCustomerId());
    }

    private String nextCardNumber() {
        String value;
        do {
            value = "9704" + String.format("%012d", random.nextLong(1_000_000_000_000L));
        } while (cards.existsByCardNumber(value));
        return value;
    }
}
