package com.hieu.corebank.shared.infrastructure.outbox;

import com.hieu.common.outbox.OutboxRecorder;
import com.hieu.corebank.shared.application.event.IntegrationEvent;
import com.hieu.corebank.shared.application.event.IntegrationEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * OutboxIntegrationEventPublisher — Adapter ghi nhận Integration Event vào bảng outbox_event (Phase 6).
 * Đảm bảo tính nhất quán (Transactional Outbox) trong cùng database transaction với nghiệp vụ.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxIntegrationEventPublisher implements IntegrationEventPublisher {

    private final OutboxRecorder outboxRecorder;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(IntegrationEvent event) {
        if (event == null) {
            return;
        }

        try {
            String payload = objectMapper.writeValueAsString(event);
            outboxRecorder.record(
                    event.getAggregateType(),
                    event.getAggregateId(),
                    event.getEventType(),
                    event.getSchemaVersion(),
                    payload
            );
            log.info("Recorded integration event into Outbox: type={} aggregateId={} schemaVersion={}",
                    event.getEventType(), event.getAggregateId(), event.getSchemaVersion());
        } catch (Exception ex) {
            log.error("Failed to serialize and record integration event {}: {}", event.getEventType(), ex.getMessage(), ex);
            throw new IllegalStateException("Failed to record integration event to outbox", ex);
        }
    }
}
