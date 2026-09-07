package com.hieu.corebank.service;

import com.hieu.corebank.api.dto.AccountDtos;
import com.hieu.corebank.domain.Account;
import com.hieu.corebank.domain.Customer;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
public class AccountService {
    private final AccountRepository accounts;
    private final CustomerService customers;
    private final SecureRandom random = new SecureRandom();
    public AccountService(AccountRepository accounts, CustomerService customers) { this.accounts = accounts; this.customers = customers; }

    @Transactional
    public Account create(AccountDtos.CreateRequest request) {
        BigDecimal balance = request.initialBalance() == null ? BigDecimal.ZERO : request.initialBalance();
        Customer customer = customers.get(request.cifNumber());
        return accounts.save(new Account(nextAccountNumber(), customer, request.currency().toUpperCase(), balance));
    }
    @Transactional(readOnly = true)
    public Account get(String id) { return accounts.findById(id).orElseThrow(() -> new NotFoundException("Account not found: " + id)); }

    @Transactional(readOnly = true)
    public java.util.List<Account> findByCif(String cifNumber) {
        customers.get(cifNumber);
        return accounts.findByCustomerCifNumberOrderByCreatedAtDesc(cifNumber.toUpperCase());
    }

    private String nextAccountNumber() {
        String value;
        do { value = "10" + String.format("%010d", random.nextLong(10_000_000_000L)); }
        while (accounts.existsByAccountNumber(value));
        return value;
    }
}
