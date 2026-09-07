package com.hieu.corebank.service;

import com.hieu.corebank.domain.BankCard;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;

@Service
public class CardService {
    private final CardRepository cards;
    private final AccountService accountService;
    private final SecureRandom random = new SecureRandom();
    public CardService(CardRepository cards, AccountService accountService) { this.cards = cards; this.accountService = accountService; }
    @Transactional
    public BankCard issue(String accountId) {
        return cards.save(new BankCard(nextCardNumber(), String.format("%03d", random.nextInt(1000)), accountService.get(accountId)));
    }
    @Transactional(readOnly = true)
    public BankCard get(String id) { return cards.findById(id).orElseThrow(() -> new NotFoundException("Card not found: " + id)); }
    @Transactional(readOnly = true)
    public java.util.List<BankCard> findByCif(String cifNumber) { return cards.findByAccountCustomerCifNumberOrderByCreatedAtDesc(cifNumber.toUpperCase()); }
    private String nextCardNumber() {
        String value;
        do { value = "9704" + String.format("%012d", random.nextLong(1_000_000_000_000L)); }
        while (cards.existsByCardNumber(value));
        return value;
    }
}
