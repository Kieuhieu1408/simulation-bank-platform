package com.hieu.corebank.transfer.domain.event;

import com.hieu.corebank.shared.domain.DomainEvent;

import java.math.BigDecimal;

public final class TransferInitiatedEvent extends DomainEvent {

    private final String sourceAccountId;
    private final String destinationAccountId;
    private final BigDecimal amount;
    private final String currency;
    private final String description;
    private final String requestHash;

    public TransferInitiatedEvent(String aggregateId, long version,
                                  String sourceAccountId, String destinationAccountId,
                                  BigDecimal amount, String currency,
                                  String description, String requestHash) {
        super(aggregateId, version);
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.requestHash = requestHash;
    }

    @Override public String eventType()     { return "TransferInitiated"; }
    @Override public String aggregateType() { return "Transfer"; }

    public String sourceAccountId()      { return sourceAccountId; }
    public String destinationAccountId() { return destinationAccountId; }
    public BigDecimal amount()           { return amount; }
    public String currency()              { return currency; }
    public String description()           { return description; }
    public String requestHash()           { return requestHash; }
}
