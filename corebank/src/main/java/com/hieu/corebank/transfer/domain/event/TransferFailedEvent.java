package com.hieu.corebank.transfer.domain.event;

import com.hieu.corebank.shared.domain.DomainEvent;

import java.math.BigDecimal;

public final class TransferFailedEvent extends DomainEvent {

    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;
    private String currency;
    private String reason;

    private TransferFailedEvent() {
        super(null, 0L);
    }

    public TransferFailedEvent(String aggregateId, long version, String reason) {
        this(aggregateId, version, null, null, null, null, reason);
    }

    public TransferFailedEvent(String aggregateId, long version,
                               String sourceAccountId, String destinationAccountId,
                               BigDecimal amount, String currency,
                               String reason) {
        super(aggregateId, version);
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.currency = currency;
        this.reason = reason;
    }

    @Override public String eventType()     { return "TransferFailed"; }
    @Override public String aggregateType() { return "Transfer"; }

    public String sourceAccountId()      { return sourceAccountId; }
    public String destinationAccountId() { return destinationAccountId; }
    public BigDecimal amount()           { return amount; }
    public String currency()              { return currency; }
    public String reason()                { return reason; }
}
