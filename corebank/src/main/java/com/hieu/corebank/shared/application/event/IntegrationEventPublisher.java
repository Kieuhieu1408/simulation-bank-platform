package com.hieu.corebank.shared.application.event;

/**
 * IntegrationEventPublisher — Application Port xuất bản sự kiện tích hợp tới Outbox (Phase 6).
 */
public interface IntegrationEventPublisher {

    void publish(IntegrationEvent event);
}
