package com.hieu.corebank.account.domain.event;

import com.hieu.corebank.shared.domain.DomainEvent;

import java.math.BigDecimal;

public final class AccountCreatedEvent extends DomainEvent {

    private final String     accountNumber;
    private final String     customerId;
    private final String     currency;
    private final BigDecimal initialBalance;

    public AccountCreatedEvent(String aggregateId, long version,
                               String accountNumber, String customerId,
                               String currency, BigDecimal initialBalance) {
        super(aggregateId, version);
        this.accountNumber  = accountNumber;
        this.customerId     = customerId;
        this.currency       = currency;
        this.initialBalance = initialBalance;
    }

    @Override public String eventType()     { return "AccountCreated"; }
    @Override public String aggregateType() { return "Account"; }

    public String     accountNumber()  { return accountNumber; }
    public String     customerId()     { return customerId; }
    public String     currency()       { return currency; }
    public BigDecimal initialBalance() { return initialBalance; }
}
