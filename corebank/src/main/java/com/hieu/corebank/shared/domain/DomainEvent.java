package com.hieu.corebank.shared.domain;

import java.time.Instant;
import java.util.UUID;

public abstract class DomainEvent {

    private final String  eventId;
    private final String  aggregateId;
    private final long    version;
    private final Instant occurredAt;

    protected DomainEvent(String aggregateId, long version) {
        this.eventId     = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.version     = version;
        this.occurredAt  = Instant.now();
    }

    public abstract String eventType();
    public abstract String aggregateType();

    public String  eventId()     { return eventId; }
    public String  aggregateId() { return aggregateId; }
    public long    version()     { return version; }
    public Instant occurredAt()  { return occurredAt; }
}
