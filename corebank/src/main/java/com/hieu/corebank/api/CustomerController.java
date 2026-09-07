package com.hieu.corebank.api;

import com.hieu.corebank.api.dto.*;
import com.hieu.corebank.service.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customers;
    private final AccountService accounts;
    private final CardService cards;

    public CustomerController(CustomerService customers, AccountService accounts, CardService cards) {
        this.customers = customers;
        this.accounts = accounts;
        this.cards = cards;
    }

    @PostMapping
    public ResponseEntity<CustomerDtos.Response> create(@Valid @RequestBody CustomerDtos.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerDtos.Response.from(customers.create(request)));
    }

    @GetMapping("/{cifNumber}")
    public CustomerDtos.Response get(@PathVariable String cifNumber) {
        return CustomerDtos.Response.from(customers.get(cifNumber));
    }

    @GetMapping("/{cifNumber}/accounts")
    public List<AccountDtos.Response> accounts(@PathVariable String cifNumber) {
        return accounts.findByCif(cifNumber).stream().map(AccountDtos.Response::from).toList();
    }

    @GetMapping("/{cifNumber}/cards")
    public List<CardDtos.Response> cards(@PathVariable String cifNumber) {
        customers.get(cifNumber);
        return cards.findByCif(cifNumber).stream().map(CardDtos.Response::from).toList();
    }
}
