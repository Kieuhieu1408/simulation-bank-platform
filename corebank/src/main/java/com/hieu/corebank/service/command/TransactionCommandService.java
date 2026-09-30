package com.hieu.corebank.service.command;

import com.hieu.corebank.domain.BankTransaction;

public interface TransactionCommandService {
    BankTransaction save(BankTransaction transaction);
    BankTransaction findByIdempotencyKey(String idempotencyKey);
}
