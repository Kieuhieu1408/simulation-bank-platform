package com.hieu.common.outbox;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Các thao tác database của dispatcher, mỗi thao tác một transaction ngắn.
 *
 * <p>Tách khỏi {@link OutboxDispatcher} vì lời gọi broker <b>không</b> được nằm
 * trong transaction database đang mở ({@code AD-DC-TX01}). Giữ transaction mở
 * trong lúc chờ network sẽ giữ connection pool và biến broker chậm thành sự cố
 * database.
 */
@Component
public class OutboxDispatchStore {

    private final OutboxEventRepository repository;
    private final OutboxProperties properties;
    private final Clock clock;

    public OutboxDispatchStore(OutboxEventRepository repository,
                               OutboxProperties properties,
                               Clock clock) {
        this.repository = repository;
        this.properties = properties;
        this.clock = clock;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<Long> findPendingIds(int batchSize) {
        return repository.findPending(clock.instant(), Limit.of(batchSize))
                .stream()
                .map(OutboxEvent::id)
                .toList();
    }

    /**
     * Giành quyền publish một event.
     *
     * @return message cần publish, hoặc rỗng nếu event đã được publish hoặc worker
     *         khác vừa giành được
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<OutboxMessage> lease(Long eventId, Duration leaseDuration) {
        Optional<OutboxEvent> found = repository.findById(eventId);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        OutboxEvent event = found.get();
        if (event.published()) {
            return Optional.empty();
        }
        try {
            event.startAttempt(clock.instant().plus(leaseDuration));
            repository.saveAndFlush(event);
            return Optional.of(OutboxMessage.from(event));
        } catch (OptimisticLockingFailureException concurrentWorker) {
            // Worker khác đã lease event này. Bỏ qua, không coi là lỗi.
            return Optional.empty();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markPublished(Long eventId) {
        repository.findById(eventId).ifPresent(event -> {
            event.markPublished(clock.instant());
            repository.save(event);
        });
    }

    /**
     * Lùi lần thử kế tiếp với backoff lũy tiến có jitter.
     *
     * <p>Jitter là bắt buộc khi có nhiều replica: backoff thuần làm các worker
     * đồng bộ hóa nhịp retry và dội vào broker theo từng đợt.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void scheduleRetry(Long eventId, String safeErrorCode) {
        repository.findById(eventId).ifPresent(event -> {
            event.scheduleRetry(clock.instant().plus(backoffFor(event.attemptCount())), safeErrorCode);
            repository.save(event);
        });
    }

    Duration backoffFor(int attemptCount) {
        long base = properties.getRetryBackoff().toMillis();
        long cap = properties.getMaxRetryBackoff().toMillis();
        // Giới hạn số mũ trước khi dịch bit để không tràn long ở attempt lớn.
        int exponent = Math.min(Math.max(attemptCount - 1, 0), 20);
        long exponential = Math.min(base << exponent, cap);
        long jitter = ThreadLocalRandom.current().nextLong(exponential / 2 + 1);
        return Duration.ofMillis(Math.min(exponential + jitter, cap));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Optional<OutboxEvent> findByEventId(String eventId) {
        return repository.findByEventId(eventId);
    }
}
