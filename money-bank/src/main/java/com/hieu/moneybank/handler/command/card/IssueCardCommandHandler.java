package com.hieu.moneybank.handler.command.card;

import com.hieu.common.cqrs.Command;
import com.hieu.common.cqrs.CommandHandler;
import com.hieu.moneybank.domain.Account;
import com.hieu.moneybank.domain.BankCard;
import com.hieu.moneybank.dto.response.CardResponseDTO;
import com.hieu.moneybank.exception.NotFoundException;
import com.hieu.moneybank.repository.AccountRepository;
import com.hieu.moneybank.repository.CardRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Component
public class IssueCardCommandHandler implements CommandHandler<IssueCardCommandHandler.IssueCardCommand, CardResponseDTO> {

    public record IssueCardCommand(String accountId) implements Command<CardResponseDTO> {
    }

    private final CardRepository cards;
    private final AccountRepository accounts;
    private final SecureRandom random = new SecureRandom();

    public IssueCardCommandHandler(CardRepository cards, AccountRepository accounts) {
        this.cards = cards;
        this.accounts = accounts;
    }

    @Override
    @Transactional
    public CardResponseDTO handle(IssueCardCommand command) {
        Account account = accounts.findById(command.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found: " + command.accountId()));
        BankCard card = cards.save(new BankCard(
                nextCardNumber(),
                String.format("%03d", random.nextInt(1000)),
                account
        ));
        return CardResponseDTO.from(card);
    }

    private String nextCardNumber() {
        String value;
        do {
            value = "9704" + String.format("%012d", random.nextLong(1_000_000_000_000L));
        } while (cards.existsByCardNumber(value));
        return value;
    }
}
