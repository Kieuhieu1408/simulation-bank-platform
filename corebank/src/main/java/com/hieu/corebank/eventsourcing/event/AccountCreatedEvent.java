package com.hieu.corebank.eventsourcing.event;

import java.math.BigDecimal;

/**
 * Tài khoản được mở lần đầu tiên.
 * actual_balance = available_balance = initialBalance.
 */
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