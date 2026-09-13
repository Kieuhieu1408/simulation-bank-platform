package com.hieu.corebank.service.command;

import com.hieu.corebank.domain.Account;

import java.util.Optional;

public interface AccountCommandService {
    Optional<Account> findByIdForUpdate(String id);
}