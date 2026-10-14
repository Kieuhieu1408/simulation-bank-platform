package com.hieu.corebank.account.application.command;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.account.api.dto.request.AccountCreateRequestDTO;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.application.port.CustomerDirectory;
import com.hieu.corebank.account.domain.AccountAggregate;
import com.hieu.corebank.account.domain.AccountNumberGenerator;
import com.hieu.corebank.account.domain.AccountRepository;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.AccountId;
import com.hieu.corebank.shared.domain.Money;
import com.hieu.corebank.shared.domain.NotFoundException;
import com.hieu.corebank.shared.domain.TransferId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * AccountHandler — Use Case xử lý tài khoản thanh toán và các nghiệp vụ sổ cái (Level 2 & Level 3 Validation).
 */
@Slf4j
@Profile({ProfileConstants.COMMAND, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class AccountHandler implements CommandHandler<AccountCreateRequestDTO, AccountResponseDTO> {

    private final CustomerDirectory customerDirectory;
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    @Override
    @Transactional
    public AccountResponseDTO handle(AccountCreateRequestDTO command) {
        String cif = command.getCifNumber().toUpperCase();
        if (!customerDirectory.existsByCif(cif)) {
            throw new NotFoundException("Customer not found: " + cif);
        }

        BigDecimal initialBalance = command.getInitialBalance() == null
                ? BigDecimal.ZERO
                : command.getInitialBalance();

        String accountId     = UUID.randomUUID().toString();
        String accountNumber = accountNumberGenerator.nextAccountNumber();

        // Tạo aggregate — sinh AccountCreatedEvent
        AccountAggregate account = AccountAggregate.create(
                accountId, accountNumber,
                cif,
                command.getCurrency().toUpperCase(),
                initialBalance
        );

        // Ghi event + outbox vào DB trong cùng transaction
        accountRepository.save(account);

        log.info("Account created: accountId={} accountNumber={} cif={}",
                accountId, accountNumber, cif);

        return AccountResponseDTO.builder()
                .accountId(accountId)
                .accountNumber(accountNumber)
                .cifNumber(cif)
                .currency(command.getCurrency().toUpperCase())
                .balance(initialBalance)
                .status(com.hieu.common.constant.AccountStatus.ACTIVE)
                .createdAt(java.time.Instant.now())
                .build();
    }

    @Transactional
    public void reserveFunds(AccountId accountId, TransferId transferId, Money amount) {
        AccountAggregate account = accountRepository.load(accountId.value());
        account.reserve(transferId, amount);
        accountRepository.save(account);
        log.info("Reserved funds on accountId={}, transferId={}, amount={}",
                accountId.value(), transferId.value(), amount);
    }

    @Transactional
    public void creditFunds(AccountId accountId, TransferId transferId, Money amount) {
        AccountAggregate account = accountRepository.load(accountId.value());
        account.credit(transferId, amount);
        accountRepository.save(account);
        log.info("Credited funds to accountId={}, transferId={}, amount={}",
                accountId.value(), transferId.value(), amount);
    }

    @Transactional
    public void settleReservation(AccountId accountId, TransferId transferId) {
        AccountAggregate account = accountRepository.load(accountId.value());
        account.settle(transferId);
        accountRepository.save(account);
        log.info("Settled reservation on accountId={}, transferId={}",
                accountId.value(), transferId.value());
    }

    @Transactional
    public void releaseReservation(AccountId accountId, TransferId transferId, String reason) {
        AccountAggregate account = accountRepository.load(accountId.value());
        account.release(transferId, reason);
        accountRepository.save(account);
        log.info("Released reservation on accountId={}, transferId={}, reason={}",
                accountId.value(), transferId.value(), reason);
    }
}
