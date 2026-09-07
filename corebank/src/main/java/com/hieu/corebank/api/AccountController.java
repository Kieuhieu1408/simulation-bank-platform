package com.hieu.corebank.api;

import com.hieu.corebank.api.dto.*;
import com.hieu.corebank.exception.BusinessException;
import com.hieu.corebank.service.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1")
public class AccountController {
    private final AccountService accounts;
    private final CardService cards;
    private final TransferService transfers;
    public AccountController(AccountService accounts, CardService cards, TransferService transfers) {
        this.accounts = accounts; this.cards = cards; this.transfers = transfers;
    }
    @PostMapping("/accounts")
    public ResponseEntity<AccountDtos.Response> create(@Valid @RequestBody AccountDtos.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountDtos.Response.from(accounts.create(request)));
    }
    @GetMapping("/accounts/{id}")
    public AccountDtos.Response get(@PathVariable String id) { return AccountDtos.Response.from(accounts.get(id)); }
    @GetMapping("/accounts/{id}/balance")
    public AccountDtos.BalanceResponse balance(@PathVariable String id) {
        var account = accounts.get(id);
        return new AccountDtos.BalanceResponse(account.getId(), account.getCurrency(), account.getBalance());
    }
    @PostMapping("/accounts/{id}/cards")
    public ResponseEntity<CardDtos.Response> issueCard(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CardDtos.Response.from(cards.issue(id)));
    }
    @GetMapping("/cards/{id}")
    public CardDtos.Response card(@PathVariable String id) { return CardDtos.Response.from(cards.get(id)); }
    @GetMapping("/accounts/{id}/transactions")
    public Page<TransferDtos.Response> history(
            @PathVariable String id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1 || size > 100) throw new BusinessException("page must be >= 0 and size must be between 1 and 100");
        return transfers.history(id, from, to, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(TransferDtos.Response::from);
    }
}
