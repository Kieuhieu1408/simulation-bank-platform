package com.hieu.corebank.handler.command.transfer;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.dto.request.TransferRequestDTO;
import com.hieu.corebank.dto.response.TransferResponseDTO;
import com.hieu.corebank.constant.AccountStatus;
import com.hieu.corebank.domain.Account;
import com.hieu.corebank.domain.BankTransaction;
import com.hieu.corebank.exception.BusinessException;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.service.command.AccountCommandService;
import com.hieu.corebank.service.command.TransactionCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TransferCommandHandler implements CommandHandler<TransferRequestDTO, TransferResponseDTO>  {

    private final AccountCommandService accountCommandService;
    private final TransactionCommandService transactionCommandService;

    @Transactional
    @Override
    public TransferResponseDTO handle(TransferRequestDTO request) {
        var existing = transactionCommandService.findByIdempotencyKey(request.getIdempotencyKey());
        if (existing != null) {
            if (!sameRequest(existing, request)) {
                throw new BusinessException("idempotencyKey was used for another transfer");
            }
            return TransferResponseDTO.from(existing);
        }

        if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
            throw new BusinessException("Source and destination accounts must be different");
        }

        // Lock in deterministic order to reduce deadlock risk.
        String firstId = request.getSourceAccountId().compareTo(request.getDestinationAccountId()) < 0
                ? request.getSourceAccountId() : request.getDestinationAccountId();
        String secondId = firstId.equals(request.getSourceAccountId())
                ? request.getDestinationAccountId() : request.getSourceAccountId();

        Account first = lock(firstId);
        Account second = lock(secondId);

        Account source = first.getId().equals(request.getSourceAccountId()) ? first : second;
        Account destination = first.getId().equals(request.getDestinationAccountId()) ? first : second;

        String currency = request.getCurrency().toUpperCase();
        if (source.getStatus() != AccountStatus.ACTIVE || destination.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("Both accounts must be ACTIVE");
        }
        if (!source.getCurrency().equals(currency) || !destination.getCurrency().equals(currency)) {
            throw new BusinessException("Transfer currency must match both accounts");
        }
        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Insufficient balance");
        }

        source.debit(request.getAmount());
        destination.credit(request.getAmount());

        BankTransaction transaction = transactionCommandService.save(new BankTransaction(
                source,
                destination,
                request.getAmount(),
                currency,
                request.getIdempotencyKey(),
                request.getDescription()
        ));

        return TransferResponseDTO.from(transaction);
    }

    private Account lock(String id) {
        return accountCommandService.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Account not found: " + id));
    }

    private boolean sameRequest(BankTransaction t, TransferRequestDTO r) {
        return t.getSourceAccount().getId().equals(r.getSourceAccountId())
                && t.getDestinationAccount().getId().equals(r.getDestinationAccountId())
                && t.getAmount().compareTo(r.getAmount()) == 0
                && t.getCurrency().equalsIgnoreCase(r.getCurrency());
    }
}