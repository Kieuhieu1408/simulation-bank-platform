package com.hieu.corebank.eventsourcing.event;

import java.math.BigDecimal;

/**
 * Tiền được nạp vào tài khoản.
 * actual_balance += amount, available_balance += amount.
 */
public final class FundsDepositedEvent extends DomainEvent {

    private final BigDecimal amount;
    private final String     currency;
    private final String     sourceTransferId;

    public FundsDepositedEvent(String aggregateId, long version,
                               BigDecimal amount, String currency, String sourceTransferId) {
        super(aggregateId, version);
        this.amount           = amount;
        this.currency         = currency;
        this.sourceTransferId = sourceTransferId;
    }

    @Override public String eventType()     { return "FundsDeposited"; }
    @Override public String aggregateType() { return "Account"; }

    public BigDecimal amount()           { return amount; }
    public String     currency()         { return currency; }
    public String     sourceTransferId() { return sourceTransferId; }
}