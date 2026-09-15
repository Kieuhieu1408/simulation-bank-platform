package com.hieu.moneybank.handler.command.account;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.moneybank.domain.Account;
import com.hieu.moneybank.domain.Customer;
import com.hieu.moneybank.dto.request.AccountCreateRequestDTO;
import com.hieu.moneybank.dto.response.AccountResponseDTO;
import com.hieu.moneybank.exception.NotFoundException;
import com.hieu.moneybank.repository.AccountRepository;
import com.hieu.moneybank.repository.CustomerRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class CreateAccountCommandHandler implements CommandHandler<AccountCreateRequestDTO, AccountResponseDTO> {

    private final AccountRepository accounts;
    private final CustomerRepository customers;
    private final JdbcClient jdbc;

    public CreateAccountCommandHandler(AccountRepository accounts,
                                       CustomerRepository customers,
                                       JdbcClient jdbc) {
        this.accounts = accounts;
        this.customers = customers;
        this.jdbc = jdbc;
    }

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
