package com.hieu.corebank.handler.command.card;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.domain.Account;
import com.hieu.corebank.domain.BankCard;
import com.hieu.corebank.dto.IssueCardRequestDTO;
import com.hieu.corebank.dto.IssueCardResponseDTO;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.repository.AccountRepository;
import com.hieu.corebank.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class IssueCardCommandHandler implements CommandHandler<IssueCardRequestDTO, IssueCardResponseDTO> {

    private final CardRepository cards;
    private final AccountRepository accounts;
    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public IssueCardResponseDTO handle(IssueCardRequestDTO command) {
        Account account = accounts.findById(command.getAccountId())
                .orElseThrow(() -> new NotFoundException("Account not found: " + command.getAccountId()));
        BankCard card = cards.save(new BankCard(
                nextCardNumber(),
                String.format("%03d", random.nextInt(1000)),
                account
        ));
        return IssueCardResponseDTO.from(card);
    }

    private String nextCardNumber() {
        String value;
        do {
            value = "9704" + String.format("%012d", random.nextLong(1_000_000_000_000L));
        } while (cards.existsByCardNumber(value));
        return value;
    }
}
