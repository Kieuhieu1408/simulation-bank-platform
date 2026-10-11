package com.hieu.corebank.account.domain.event;

import com.hieu.corebank.shared.domain.DomainEvent;

import java.math.BigDecimal;

public final class FundsReservedEvent extends DomainEvent {

    private final BigDecimal amount;
    private final String     currency;
    private final String     transferId;

    public FundsReservedEvent(String aggregateId, long version,
                              BigDecimal amount, String currency, String transferId) {
        super(aggregateId, version);
        this.amount     = amount;
        this.currency   = currency;
        this.transferId = transferId;
    }

    @Override public String eventType()     { return "FundsReserved"; }
    @Override public String aggregateType() { return "Account"; }

    public BigDecimal amount()     { return amount; }
    public String     currency()   { return currency; }
    public String     transferId() { return transferId; }
}
