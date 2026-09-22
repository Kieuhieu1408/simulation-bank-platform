package com.hieu.common.outbox;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Đẩy event từ Outbox ra broker.
 *
 * <p>Trình tự cho mỗi event: lease trong một transaction ngắn, publish ngoài
 * transaction, rồi chốt kết quả trong một transaction ngắn khác. Không có bước
 * nào gọi broker khi transaction database đang mở ({@code AD-DC-TX01}).
 *
 * <p>Bảo đảm cung cấp là at-least-once, không phải exactly-once. Nếu process chết
 * giữa lúc broker đã nhận và lúc ghi {@code publishedAt}, event sẽ được gửi lại.
 * Vì vậy mọi consumer phải chống trùng bằng Inbox ({@code AD-DC-O02}).
 */
import org.springframework.stereotype.Component;

@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnBean(OutboxMessagePublisher.class)
public class OutboxDispatcher {

    private static final Logger log = LoggerFactory.getLogger(OutboxDispatcher.class);

    private static final String METRIC_PUBLISH = "moneybank.outbox.publish";
    private static final String TAG_RESULT = "result";

    private final OutboxDispatchStore store;
    private final OutboxMessagePublisher publisher;
    private final OutboxProperties properties;
    private final MeterRegistry meterRegistry;

    public OutboxDispatcher(OutboxDispatchStore store,
                            OutboxMessagePublisher publisher,
                            OutboxProperties properties,
                            MeterRegistry meterRegistry) {
        this.store = store;
        this.publisher = publisher;
        this.properties = properties;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Xử lý một lô event đang chờ.
     *
     * @return số event đã publish thành công trong lô này
     */
    public int dispatchBatch() {
        List<Long> pendingIds = store.findPendingIds(properties.getBatchSize());
        int published = 0;
        for (Long id : pendingIds) {
            if (dispatchOne(id)) {
                published++;
            }
        }
        return published;
    }

    private boolean dispatchOne(Long id) {
        Optional<OutboxMessage> leased = store.lease(id, leaseDuration());
        if (leased.isEmpty()) {
            return false;
        }
        OutboxMessage message = leased.get();
        try {
            publisher.publish(message);
        } catch (RuntimeException failure) {
            // Không rethrow: một event lỗi không được làm dừng cả lô. Event vẫn còn
            // trong Outbox nên không mất, chỉ bị lùi lần thử.
            handleFailure(id, message, failure);
            return false;
        }
        store.markPublished(id);
        meterRegistry.counter(METRIC_PUBLISH, TAG_RESULT, "published").increment();
        return true;
    }

    private void handleFailure(Long id, OutboxMessage message, RuntimeException failure) {
        // Backoff được tính trong store vì ở đó có attemptCount hiện tại của event.
        store.scheduleRetry(id, safeErrorCode(failure));
        meterRegistry.counter(METRIC_PUBLISH, TAG_RESULT, "failed").increment();
        log.warn("eventName={} eventType={} eventId={} errorCode={}",
                "OUTBOX_PUBLISH_FAILED",
                message.eventType(),
                message.eventId(),
                safeErrorCode(failure));
    }

    /** Chỉ giữ tên loại exception, không đưa message gốc vào cột lưu trữ. */
    private String safeErrorCode(RuntimeException failure) {
        return failure.getClass().getSimpleName();
    }

    private Duration leaseDuration() {
        // Lease dài hơn thời gian publish kỳ vọng nhưng đủ ngắn để event được thử
        // lại sớm nếu worker chết.
        return properties.getRetryBackoff();
    }
}
