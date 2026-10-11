package com.hieu.corebank.account.domain.event;

import com.hieu.corebank.shared.domain.DomainEvent;

import java.math.BigDecimal;

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
