package com.hieu.corebank.service.command;

import com.hieu.corebank.domain.BankTransaction;

public interface TransactionCommandService {
    BankTransaction findByIdempotencyKey(String idempotencyKey);
    BankTransaction save(BankTransaction transaction);
}