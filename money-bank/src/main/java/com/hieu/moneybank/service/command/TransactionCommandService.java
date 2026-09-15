package com.hieu.moneybank.service.command;

import com.hieu.moneybank.domain.BankTransaction;

public interface TransactionCommandService {
    BankTransaction findByIdempotencyKey(String idempotencyKey);
    BankTransaction save(BankTransaction transaction);
}