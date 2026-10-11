package com.hieu.corebank.shared.application.event;

import com.hieu.corebank.shared.domain.DomainEvent;

import java.util.Optional;

/**
 * IntegrationEventMapper — Cổng chuyển đổi từ Domain Event nội bộ sang Integration Event công khai (Phase 6).
 * Ngăn chặn rò rỉ payload kỹ thuật nội bộ ra ngoài hệ thống và các kênh Kafka.
 */
public interface IntegrationEventMapper {

    Optional<IntegrationEvent> toIntegrationEvent(DomainEvent domainEvent);
}
