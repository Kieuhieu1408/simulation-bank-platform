package com.hieu.corebank.eventsourcing.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieu.corebank.eventsourcing.event.DomainEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * JPA entity mapping bảng domain_events.
 * Chỉ được INSERT — không bao giờ UPDATE hay DELETE.
 */
@Getter
@NoArgsConstructor
@Entity
@Table(
    name = "domain_events",
    uniqueConstraints = @UniqueConstraint(name = "uq_aggregate_version",
            columnNames = {"aggregate_id", "version"})
)
public class DomainEventRecord {

    @Id
    @Column(name = "event_id", length = 50, nullable = false, updatable = false)
    private String eventId;

    @Column(name = "aggregate_id", length = 50, nullable = false, updatable = false)
    private String aggregateId;

    @Column(name = "aggregate_type", length = 50, nullable = false, updatable = false)
    private String aggregateType;

    @Column(name = "version", nullable = false, updatable = false)
    private long version;

    @Column(name = "event_type", length = 100, nullable = false, updatable = false)
    private String eventType;

    @Lob
    @Column(name = "payload", nullable = false, updatable = false)
    private String payload;

    @Lob
    @Column(name = "metadata", updatable = false)
    private String metadata;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    private DomainEventRecord(String eventId, String aggregateId, String aggregateType,
                              long version, String eventType, String payload,
                              String metadata, Instant occurredAt) {
        this.eventId       = eventId;
        this.aggregateId   = aggregateId;
        this.aggregateType = aggregateType;
        this.version       = version;
        this.eventType     = eventType;
        this.payload       = payload;
        this.metadata      = metadata;
        this.occurredAt    = occurredAt;
    }

    public static DomainEventRecord from(DomainEvent event, ObjectMapper mapper) {
        try {
            String payload = mapper.writeValueAsString(event);
            return new DomainEventRecord(
                    event.eventId(),
                    event.aggregateId(),
                    event.aggregateType(),
                    event.version(),
                    event.eventType(),
                    payload,
                    null,
                    event.occurredAt()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize event: " + event.eventType(), e);
        }
    }
}