package com.hieu.common.outbox;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Ghi event vào Outbox.
 *
 * <p>Bắt buộc chạy trong transaction của use case ({@code Propagation.MANDATORY}).
 * Đây là điểm cốt lõi của pattern: nếu event được ghi ở transaction riêng, nó có
 * thể commit trong khi state change rollback, và hệ thống sẽ thông báo một giao
 * dịch chưa từng xảy ra. {@code MANDATORY} biến sai sót đó thành lỗi ngay khi
 * chạy thay vì một bug âm thầm trong production.
 */
@Component
public class OutboxRecorder {

    private final OutboxEventRepository repository;
    private final Clock clock;

    public OutboxRecorder(OutboxEventRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    /**
     * Ghi một event mới vào Outbox trong transaction hiện tại.
     *
     * @param aggregateType loại aggregate, ví dụ {@code TRANSFER}
     * @param aggregateId   định danh aggregate, ví dụ {@code transactionId}
     * @param eventType     tên event, ví dụ {@code TransferSucceeded}
     * @param schemaVersion version schema của payload
     * @param payload       payload đã sanitize
     * @throws IllegalStateException nếu không có transaction đang mở
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public OutboxEvent record(String aggregateType,
                              String aggregateId,
                              String eventType,
                              String schemaVersion,
                              String payload) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            // Lớp bảo vệ thứ hai bên cạnh MANDATORY, để lỗi cấu hình transaction
            // không biến thành mất tính nhất quán.
            throw new IllegalStateException("Outbox chỉ được ghi trong transaction nghiệp vụ");
        }
        OutboxEvent event = OutboxEvent.pending(
                UUID.randomUUID().toString(),
                aggregateType,
                aggregateId,
                eventType,
                schemaVersion,
                payload,
                clock.instant());
        return repository.save(event);
    }
}
