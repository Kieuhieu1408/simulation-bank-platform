package com.hieu.moneybank.service.command.impl;

import com.hieu.moneybank.domain.BankTransaction;
import com.hieu.moneybank.repository.TransactionRepository;
import com.hieu.moneybank.service.command.TransactionCommandService;
import org.springframework.stereotype.Service;

@Service
public class TransactionCommandServiceImpl implements TransactionCommandService {

    private final TransactionRepository transactionRepository;

    public TransactionCommandServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public BankTransaction findByIdempotencyKey(String idempotencyKey) {
        return transactionRepository.findByIdempotencyKey(idempotencyKey);
    }

    @Override
    public BankTransaction save(BankTransaction transaction) {
        return transactionRepository.save(transaction);
    }
}