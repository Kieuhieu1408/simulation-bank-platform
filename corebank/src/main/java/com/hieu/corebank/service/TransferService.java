package com.hieu.corebank.service;

import com.hieu.corebank.api.dto.TransferDtos;
import com.hieu.corebank.constant.AccountStatus;
import com.hieu.corebank.domain.*;
import com.hieu.corebank.exception.BusinessException;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class TransferService {
    private final AccountRepository accounts;
    private final TransactionRepository transactions;
    public TransferService(AccountRepository accounts, TransactionRepository transactions) {
        this.accounts = accounts; this.transactions = transactions;
    }

    @Transactional
    public BankTransaction transfer(TransferDtos.Request request) {
        var existing = transactions.findByIdempotencyKey(request.idempotencyKey());
        if (existing.isPresent()) {
            BankTransaction transaction = existing.get();
            if (!sameRequest(transaction, request)) throw new BusinessException("idempotencyKey was used for another transfer");
            return transaction;
        }
        if (request.sourceAccountId().equals(request.destinationAccountId()))
            throw new BusinessException("Source and destination accounts must be different");

        // Lock in deterministic order to reduce deadlock risk.
        String firstId = request.sourceAccountId().compareTo(request.destinationAccountId()) < 0
                ? request.sourceAccountId() : request.destinationAccountId();
        String secondId = firstId.equals(request.sourceAccountId()) ? request.destinationAccountId() : request.sourceAccountId();
        Account first = lock(firstId);
        Account second = lock(secondId);
        Account source = first.getId().equals(request.sourceAccountId()) ? first : second;
        Account destination = first.getId().equals(request.destinationAccountId()) ? first : second;

        String currency = request.currency().toUpperCase();
        if (source.getStatus() != AccountStatus.ACTIVE || destination.getStatus() != AccountStatus.ACTIVE)
            throw new BusinessException("Both accounts must be ACTIVE");
        if (!source.getCurrency().equals(currency) || !destination.getCurrency().equals(currency))
            throw new BusinessException("Transfer currency must match both accounts");
        if (source.getBalance().compareTo(request.amount()) < 0)
            throw new BusinessException("Insufficient balance");

        source.debit(request.amount());
        destination.credit(request.amount());
        return transactions.save(new BankTransaction(source, destination, request.amount(), currency,
                request.idempotencyKey(), request.description()));
    }

    @Transactional(readOnly = true)
    public Page<BankTransaction> history(String accountId, Instant from, Instant to, Pageable pageable) {
        if (!accounts.existsById(accountId)) throw new NotFoundException("Account not found: " + accountId);
        Instant start = from == null ? Instant.EPOCH : from;
        Instant end = to == null ? Instant.now() : to;
        if (start.isAfter(end)) throw new BusinessException("from must be before to");
        return transactions.findHistory(accountId, start, end, pageable);
    }
    private Account lock(String id) { return accounts.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Account not found: " + id)); }
    private boolean sameRequest(BankTransaction t, TransferDtos.Request r) {
        return t.getSourceAccount().getId().equals(r.sourceAccountId())
                && t.getDestinationAccount().getId().equals(r.destinationAccountId())
                && t.getAmount().compareTo(r.amount()) == 0
                && t.getCurrency().equalsIgnoreCase(r.currency());
    }
}
