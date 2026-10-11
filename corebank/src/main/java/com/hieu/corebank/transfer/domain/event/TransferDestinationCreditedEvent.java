package com.hieu.corebank.transfer.domain.event;

import com.hieu.corebank.shared.domain.DomainEvent;

public final class TransferDestinationCreditedEvent extends DomainEvent {

    public TransferDestinationCreditedEvent(String aggregateId, long version) {
        super(aggregateId, version);
    }

    @Override public String eventType()     { return "TransferDestinationCredited"; }
    @Override public String aggregateType() { return "Transfer"; }
}
