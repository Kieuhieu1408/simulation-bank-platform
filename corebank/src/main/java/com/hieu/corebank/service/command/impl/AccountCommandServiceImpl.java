package com.hieu.corebank.service.command.impl;

import com.hieu.corebank.domain.Account;
import com.hieu.corebank.repository.AccountRepository;
import com.hieu.corebank.service.command.AccountCommandService;
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
}