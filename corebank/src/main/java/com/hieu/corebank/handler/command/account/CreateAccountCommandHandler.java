package com.hieu.corebank.handler.command.account;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.domain.Account;
import com.hieu.corebank.domain.Customer;
import com.hieu.corebank.dto.AccountCreateRequestDTO;
import com.hieu.corebank.dto.AccountResponseDTO;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.repository.AccountRepository;
import com.hieu.corebank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CreateAccountCommandHandler implements CommandHandler<AccountCreateRequestDTO, AccountResponseDTO> {

    private final AccountRepository accounts;
    private final CustomerRepository customers;
    private final JdbcClient jdbc;

    @Override
    @Transactional
    public AccountResponseDTO handle(AccountCreateRequestDTO request) {
        BigDecimal balance = request.getInitialBalance() == null ? BigDecimal.ZERO : request.getInitialBalance();
        Customer customer = customers.findById(request.getCifNumber())
                .orElseThrow(() -> new NotFoundException("Customer not found: " + request.getCifNumber()));
        Account account = accounts.save(new Account(
                nextAccountNumber(),
                customer,
                request.getCurrency().toUpperCase(),
                balance
        ));
        return AccountResponseDTO.from(account);
    }

    private String nextAccountNumber() {
        long seq = jdbc.sql("SELECT account_number_seq.NEXTVAL FROM DUAL")
                .query(Long.class)
                .single();
        return "10" + String.format("%010d", seq);
    }
}
