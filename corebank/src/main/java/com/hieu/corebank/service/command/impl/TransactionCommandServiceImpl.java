package com.hieu.corebank.service.command.impl;

import com.hieu.corebank.domain.BankTransaction;
import com.hieu.corebank.repository.TransactionRepository;
import com.hieu.corebank.service.command.TransactionCommandService;
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