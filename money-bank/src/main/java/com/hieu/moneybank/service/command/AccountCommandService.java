package com.hieu.moneybank.service.command;

import com.hieu.moneybank.domain.Account;

import java.util.Optional;

public interface AccountCommandService {
    Optional<Account> findByIdForUpdate(String id);
    Optional<Account> findById(String id);
}
