package com.hieu.corebank.transfer.domain.event;

import com.hieu.corebank.shared.domain.DomainEvent;

public final class TransferFundsReservedEvent extends DomainEvent {

    public TransferFundsReservedEvent(String aggregateId, long version) {
        super(aggregateId, version);
    }

    @Override public String eventType()     { return "TransferFundsReserved"; }
    @Override public String aggregateType() { return "Transfer"; }
}
