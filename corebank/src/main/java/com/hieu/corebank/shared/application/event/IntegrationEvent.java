package com.hieu.corebank.shared.application.event;

import java.time.Instant;

/**
 * IntegrationEvent — Hợp đồng công khai cho các sự kiện giao tiếp liên dịch vụ (Public Contract).
 * Tuân thủ nguyên tắc cách ly giữa Domain Event nội bộ và Integration Event đẩy ra Kafka / Outbox (Phase 6).
 */
public interface IntegrationEvent {

    String getEventId();

    String getEventType();

    String getAggregateType();

    String getAggregateId();

    String getSchemaVersion();

    Instant getOccurredAt();
}
