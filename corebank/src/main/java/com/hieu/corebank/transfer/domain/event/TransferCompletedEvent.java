package com.hieu.corebank.transfer.domain.event;

import com.hieu.corebank.shared.domain.DomainEvent;

import java.math.BigDecimal;

public final class TransferCompletedEvent extends DomainEvent {

    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;
    private String currency;

    private TransferCompletedEvent() {
        super(null, 0L);
    }

    public TransferCompletedEvent(String aggregateId, long version) {
        this(aggregateId, version, null, null, null, null);
    }

    public TransferCompletedEvent(String aggregateId, long version,
                                  String sourceAccountId, String destinationAccountId,
                                  BigDecimal amount, String currency) {
        super(aggregateId, version);
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.currency = currency;
    }

    @Override public String eventType()     { return "TransferCompleted"; }
    @Override public String aggregateType() { return "Transfer"; }

    public String sourceAccountId()      { return sourceAccountId; }
    public String destinationAccountId() { return destinationAccountId; }
    public BigDecimal amount()           { return amount; }
    public String currency()              { return currency; }
}
