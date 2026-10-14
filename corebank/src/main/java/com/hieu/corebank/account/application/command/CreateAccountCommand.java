package com.hieu.corebank.account.application.command;

import com.hieu.corebank.account.api.dto.request.AccountCreateRequestDTO;

import java.math.BigDecimal;

public class CreateAccountCommand extends AccountCreateRequestDTO {
    public CreateAccountCommand(String cifNumber, String currency, BigDecimal initialBalance) {
        super(cifNumber, currency, initialBalance);
    }

    public String cifNumber() {
        return getCifNumber();
    }

    public String currency() {
        return getCurrency();
    }

    public BigDecimal initialBalance() {
        return getInitialBalance();
    }
}
