package com.hieu.common.outbox;

import java.time.Instant;

/**
 * Message được đẩy ra broker.
 *
 * <p>Tách khỏi {@link OutboxEvent} để adapter messaging không phụ thuộc JPA
 * entity: entity có trạng thái publish/attempt là chuyện nội bộ của Money Bank,
 * còn message là contract đi ra ngoài.
 */
public record OutboxMessage(
        String eventId,
        String aggregateType,
        String aggregateId,
        String eventType,
        String schemaVersion,
        String payload,
        Instant occurredAt) {

    static OutboxMessage from(OutboxEvent event) {
        return new OutboxMessage(
                event.eventId(),
                event.aggregateType(),
                event.aggregateId(),
                event.eventType(),
                event.schemaVersion(),
                event.payload(),
                event.occurredAt());
    }
}
