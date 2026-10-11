package com.hieu.corebank.transfer.infrastructure.persistence;

import com.hieu.corebank.shared.application.event.IntegrationEventMapper;
import com.hieu.corebank.shared.application.event.IntegrationEventPublisher;
import com.hieu.corebank.shared.domain.DomainEvent;
import com.hieu.corebank.shared.domain.TransferId;
import com.hieu.corebank.shared.infrastructure.eventsourcing.DomainEventRecord;
import com.hieu.corebank.shared.infrastructure.eventsourcing.DomainEventRepository;
import com.hieu.corebank.transfer.domain.Transfer;
import com.hieu.corebank.transfer.domain.TransferRepository;
import com.hieu.corebank.transfer.domain.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class EventSourcedTransferRepository implements TransferRepository {

    private final DomainEventRepository domainEventRepository;
    private final IntegrationEventPublisher integrationEventPublisher;
    private final IntegrationEventMapper integrationEventMapper;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    public EventSourcedTransferRepository(DomainEventRepository domainEventRepository,
                                          IntegrationEventPublisher integrationEventPublisher,
                                          IntegrationEventMapper integrationEventMapper,
                                          ObjectMapper objectMapper,
                                          ApplicationEventPublisher eventPublisher) {
        this.domainEventRepository = domainEventRepository;
        this.integrationEventPublisher = integrationEventPublisher;
        this.integrationEventMapper = integrationEventMapper;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper.rebuild()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .changeDefaultVisibility(vc -> vc
                        .withFieldVisibility(com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY))
                .build();
    }

    @Override
    @Transactional
    public void save(Transfer transfer) {
        List<DomainEvent> events = transfer.uncommittedEvents();
        if (events.isEmpty()) return;

        for (DomainEvent event : events) {
            DomainEventRecord record = DomainEventRecord.from(event, objectMapper);
            domainEventRepository.save(record);

            // Ghi nhận Integration Event vào Outbox (nếu domain event có mapping công khai - Phase 6)
            integrationEventMapper.toIntegrationEvent(event)
                    .ifPresent(integrationEventPublisher::publish);

            log.debug("Appended transfer event: type={} aggregateId={} version={}",
                    event.eventType(), event.aggregateId(), event.version());

            eventPublisher.publishEvent(event);
        }

        transfer.markCommitted();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transfer> findById(TransferId id) {
        List<DomainEventRecord> records = domainEventRepository
                .findByAggregateIdOrderByVersionAsc(id.value());
        if (records.isEmpty()) {
            return Optional.empty();
        }

        List<DomainEvent> history = new ArrayList<>();
        for (DomainEventRecord record : records) {
            history.add(deserialize(record));
        }

        return Optional.of(Transfer.reconstitute(history));
    }

    private DomainEvent deserialize(DomainEventRecord record) {
        try {
            return switch (record.getEventType()) {
                case "TransferInitiated" -> objectMapper.readValue(record.getPayload(), TransferInitiatedEvent.class);
                case "TransferFundsReserved" -> objectMapper.readValue(record.getPayload(), TransferFundsReservedEvent.class);
                case "TransferDestinationCredited" -> objectMapper.readValue(record.getPayload(), TransferDestinationCreditedEvent.class);
                case "TransferCompleted" -> objectMapper.readValue(record.getPayload(), TransferCompletedEvent.class);
                case "TransferFailed" -> objectMapper.readValue(record.getPayload(), TransferFailedEvent.class);
                default -> throw new IllegalArgumentException("Unknown transfer event type: " + record.getEventType());
            };
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize transfer event: " + record.getEventId(), e);
        }
    }
}
