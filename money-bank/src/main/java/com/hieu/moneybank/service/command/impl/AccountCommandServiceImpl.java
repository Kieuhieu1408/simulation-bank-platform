package com.hieu.moneybank.service.command.impl;

import com.hieu.moneybank.domain.Account;
import com.hieu.moneybank.repository.AccountRepository;
import com.hieu.moneybank.service.command.AccountCommandService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountCommandServiceImpl implements AccountCommandService {

    private final AccountRepository accountRepository;

    public AccountCommandServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> findByIdForUpdate(String id) {
        return accountRepository.findByIdForUpdate(id);
    }
    
    @Override
    public Optional<Account> findById(String id) {
        return accountRepository.findById(id);
    }
}
