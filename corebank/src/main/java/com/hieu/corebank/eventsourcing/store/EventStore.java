package com.hieu.corebank.eventsourcing.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieu.corebank.eventsourcing.aggregate.AccountAggregate;
import com.hieu.corebank.eventsourcing.event.*;
import com.hieu.corebank.eventsourcing.snapshot.AggregateSnapshot;
import com.hieu.corebank.eventsourcing.snapshot.SnapshotRepository;
import com.hieu.common.outbox.OutboxRecorder;
import com.hieu.corebank.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * EventStore — bộ máy trung tâm của Write Side.
 *
 * <p>Chịu trách nhiệm:
 * <ul>
 *   <li>load() — tái tạo AccountAggregate từ Snapshot + Event history</li>
 *   <li>append() — lưu uncommittedEvents vào domain_events và ghi vào Outbox</li>
 * </ul>
 *
 * <p>Optimistic Locking được DB thực thi qua UNIQUE(aggregate_id, version).
 * Nếu 2 thread cùng ghi version=6 cho cùng account, DB bắn
 * DataIntegrityViolationException → caller bắt lỗi và retry.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventStore {

    private final DomainEventRepository eventRepository;
    private final SnapshotRepository    snapshotRepository;
    private final OutboxRecorder        outboxRecorder;
    private final ObjectMapper          objectMapper;

    // =========================================================================
    // Load — tái tạo Aggregate từ lịch sử Event
    // =========================================================================

    /**
     * Nạp AccountAggregate từ Event Store.
     * Ưu tiên Snapshot nếu có, sau đó replay các event mới hơn.
     *
     * @throws NotFoundException nếu không tìm thấy bất kỳ event nào cho accountId
     */
    @Transactional(readOnly = true)
    public AccountAggregate load(String accountId) {
        Optional<AggregateSnapshot> snapshot = snapshotRepository.findById(accountId);

        long fromVersion = snapshot.map(AggregateSnapshot::getVersion).orElse(0L);

        List<DomainEventRecord> records = eventRepository
                .findByAggregateIdAndVersionGreaterThanOrderByVersionAsc(accountId, fromVersion);

        if (snapshot.isEmpty() && records.isEmpty()) {
            throw new NotFoundException("Account not found in event store: " + accountId);
        }

        List<DomainEvent> history = new ArrayList<>();

        // Nếu có snapshot thì rebuild base state trước
        if (snapshot.isPresent()) {
            history.addAll(deserializeSnapshot(snapshot.get()));
        }

        // Append các event mới hơn snapshot
        for (DomainEventRecord record : records) {
            history.add(deserialize(record));
        }

        return AccountAggregate.reconstitute(history);
    }

    // =========================================================================
    // Append — lưu uncommitted events + outbox (trong cùng 1 transaction)
    // =========================================================================

    /**
     * Lưu tất cả uncommittedEvents của aggregate vào:
     * 1. Bảng domain_events (nguồn sự thật)
     * 2. Bảng outbox_event (để Outbox Dispatcher phát đi sau)
     *
     * <p>MANDATORY: phải chạy trong transaction của CommandHandler.
     *
     * @throws DataIntegrityViolationException nếu version bị conflict (Optimistic Lock)
     */
    @Transactional
    public void append(AccountAggregate aggregate) {
        List<DomainEvent> events = aggregate.uncommittedEvents();
        if (events.isEmpty()) return;

        for (DomainEvent event : events) {
            DomainEventRecord record = DomainEventRecord.from(event, objectMapper);
            eventRepository.save(record);

            // Ghi vào Outbox trong cùng transaction → at-least-once delivery
            outboxRecorder.record(
                    event.aggregateType(),
                    event.aggregateId(),
                    event.eventType(),
                    "1",
                    record.getPayload()
            );

            log.debug("Appended event: type={} aggregateId={} version={}",
                    event.eventType(), event.aggregateId(), event.version());
        }

        // Chụp Snapshot nếu đủ ngưỡng
        if (aggregate.shouldSnapshot()) {
            takeSnapshot(aggregate);
        }

        aggregate.markCommitted();
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private void takeSnapshot(AccountAggregate agg) {
        try {
            String payload = objectMapper.writeValueAsString(new SnapshotPayload(
                    agg.accountId(), agg.accountNumber(), agg.customerId(),
                    agg.currency(), agg.actualBalance(), agg.availableBalance(),
                    agg.status().name()
            ));
            AggregateSnapshot snap = new AggregateSnapshot(
                    agg.accountId(), "Account", agg.version(), payload);
            snapshotRepository.save(snap);
            log.info("Snapshot taken: accountId={} version={}", agg.accountId(), agg.version());
        } catch (Exception e) {
            // Snapshot thất bại không ảnh hưởng tới correctness, chỉ ảnh hưởng perf
            log.warn("Failed to take snapshot for accountId={}: {}", agg.accountId(), e.getMessage());
        }
    }

    private List<DomainEvent> deserializeSnapshot(AggregateSnapshot snapshot) {
        // Snapshot không phải event — ta rebuild từ nó bằng cách tạo 1 AccountCreatedEvent tổng hợp
        try {
            SnapshotPayload p = objectMapper.readValue(snapshot.getPayload(), SnapshotPayload.class);
            return List.of(new AccountCreatedEvent(
                    p.accountId(), snapshot.getVersion(),
                    p.accountNumber(), p.customerId(),
                    p.currency(), p.actualBalance()));
        } catch (Exception e) {
            log.error("Failed to deserialize snapshot for aggregateId={}", snapshot.getAggregateId(), e);
            return List.of();
        }
    }

    private DomainEvent deserialize(DomainEventRecord record) {
        try {
            return switch (record.getEventType()) {
                case "AccountCreated"    -> objectMapper.readValue(record.getPayload(), AccountCreatedEvent.class);
                case "FundsReserved"     -> objectMapper.readValue(record.getPayload(), FundsReservedEvent.class);
                case "TransferCompleted" -> objectMapper.readValue(record.getPayload(), TransferCompletedEvent.class);
                case "TransferFailed"    -> objectMapper.readValue(record.getPayload(), TransferFailedEvent.class);
                case "FundsDeposited"    -> objectMapper.readValue(record.getPayload(), FundsDepositedEvent.class);
                default -> throw new IllegalArgumentException("Unknown event type: " + record.getEventType());
            };
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize event: " + record.getEventId(), e);
        }
    }

    // Inner record cho snapshot payload — dùng Jackson deserialization
    record SnapshotPayload(
            String accountId, String accountNumber, String customerId,
            String currency, java.math.BigDecimal actualBalance,
            java.math.BigDecimal availableBalance, String status) {}
}