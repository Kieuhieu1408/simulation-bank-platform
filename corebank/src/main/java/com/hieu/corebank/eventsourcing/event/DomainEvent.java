package com.hieu.corebank.eventsourcing.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class cho tất cả Domain Events trong hệ thống Event Sourcing.
 * Mỗi event là một FACT đã xảy ra — immutable, append-only.
 */
public abstract class DomainEvent {

    private final String eventId;
    private final String aggregateId;
    private final long   version;
    private final Instant occurredAt;

    protected DomainEvent(String aggregateId, long version) {
        this.eventId     = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.version     = version;
        this.occurredAt  = Instant.now();
    }

    public String eventId()     { return eventId; }
    public String aggregateId() { return aggregateId; }
    public long   version()     { return version; }
    public Instant occurredAt() { return occurredAt; }

    /** Tên event dùng để lưu vào cột event_type trong domain_events. */
    public abstract String eventType();

    /** Tên aggregate dùng để lưu vào cột aggregate_type trong domain_events. */
    public abstract String aggregateType();
}