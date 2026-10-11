package com.hieu.corebank.account.application.command;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.application.port.CustomerDirectory;
import com.hieu.corebank.account.domain.AccountAggregate;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.NotFoundException;
import com.hieu.corebank.shared.infrastructure.eventsourcing.EventStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * CreateAccountCommandHandler V2 — Event Sourcing edition.
 */
@Slf4j
@Profile({ProfileConstants.COMMAND, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class CreateAccountCommandHandler implements CommandHandler<CreateAccountCommand, AccountResponseDTO> {

    private final CustomerDirectory customerDirectory;
    private final EventStore        eventStore;
    private final JdbcClient        jdbc;

    @Override
    @Transactional
    public AccountResponseDTO handle(CreateAccountCommand command) {
        String cif = command.cifNumber().toUpperCase();
        if (!customerDirectory.existsByCif(cif)) {
            throw new NotFoundException("Customer not found: " + cif);
        }

        BigDecimal initialBalance = command.initialBalance() == null
                ? BigDecimal.ZERO
                : command.initialBalance();

        String accountId     = UUID.randomUUID().toString();
        String accountNumber = nextAccountNumber();

        // Tạo aggregate — sinh AccountCreatedEvent
        AccountAggregate account = AccountAggregate.create(
                accountId, accountNumber,
                cif,
                command.currency().toUpperCase(),
                initialBalance
        );

        // Ghi event + outbox vào DB trong cùng transaction
        eventStore.append(account);

        log.info("Account created: accountId={} accountNumber={} cif={}",
                accountId, accountNumber, cif);

        return AccountResponseDTO.builder()
                .accountId(accountId)
                .accountNumber(accountNumber)
                .cifNumber(cif)
                .currency(command.currency().toUpperCase())
                .balance(initialBalance)
                .status(com.hieu.common.constant.AccountStatus.ACTIVE)
                .createdAt(java.time.Instant.now())
                .build();
    }

    private String nextAccountNumber() {
        long seq = jdbc.sql("SELECT account_number_seq.NEXTVAL FROM DUAL")
                .query(Long.class)
                .single();
        return "10" + String.format("%010d", seq);
    }
}
