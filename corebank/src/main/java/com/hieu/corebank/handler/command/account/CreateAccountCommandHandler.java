package com.hieu.corebank.handler.command.account;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.domain.Customer;
import com.hieu.corebank.dto.request.AccountCreateRequestDTO;
import com.hieu.corebank.dto.response.AccountResponseDTO;
import com.hieu.corebank.eventsourcing.aggregate.AccountAggregate;
import com.hieu.corebank.eventsourcing.store.EventStore;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * CreateAccountCommandHandler V2 — Event Sourcing edition.
 *
 * <p>Thay vì accounts.save(new Account(...)), giờ ta:
 * 1. Tạo AccountAggregate (sinh AccountCreatedEvent)
 * 2. EventStore.append() ghi event + outbox vào DB
 *
 * <p>account_view sẽ được cập nhật bởi AccountProjector khi nhận AccountCreatedEvent.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreateAccountCommandHandler implements CommandHandler<AccountCreateRequestDTO, AccountResponseDTO> {

    private final CustomerRepository customers;
    private final EventStore          eventStore;
    private final JdbcClient          jdbc;

    @Override
    @Transactional
    public AccountResponseDTO handle(AccountCreateRequestDTO request) {
        Customer customer = customers.findById(request.getCifNumber())
                .orElseThrow(() -> new NotFoundException("Customer not found: " + request.getCifNumber()));

        BigDecimal initialBalance = request.getInitialBalance() == null
                ? BigDecimal.ZERO
                : request.getInitialBalance();

        String accountId     = UUID.randomUUID().toString();
        String accountNumber = nextAccountNumber();

        // Tạo aggregate — sinh AccountCreatedEvent
        AccountAggregate account = AccountAggregate.create(
                accountId, accountNumber,
                customer.getCifNumber(),
                request.getCurrency().toUpperCase(),
                initialBalance
        );

        // Ghi event + outbox vào DB trong cùng transaction
        eventStore.append(account);

        log.info("Account created: accountId={} accountNumber={} cif={}",
                accountId, accountNumber, customer.getCifNumber());

        return AccountResponseDTO.builder()
                .accountId(accountId)
                .accountNumber(accountNumber)
                .cifNumber(customer.getCifNumber())
                .currency(request.getCurrency().toUpperCase())
                .balance(initialBalance)
                .status(com.hieu.corebank.constant.AccountStatus.ACTIVE)
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