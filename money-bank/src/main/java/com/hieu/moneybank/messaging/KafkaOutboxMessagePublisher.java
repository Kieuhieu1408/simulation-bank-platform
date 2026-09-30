package com.hieu.moneybank.messaging;

import com.hieu.common.outbox.OutboxMessage;
import com.hieu.common.outbox.OutboxMessagePublisher;
import com.hieu.moneybank.config.OutboxKafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Kafka adapter cho {@link OutboxMessagePublisher} — implements U-07.
 *
 * <h3>Delivery guarantee</h3>
 * <p>Gọi {@code future.get(timeout)} theo nghĩa blocking: chỉ trả về bình thường
 * khi broker đã ack. Nếu broker không ack trong timeout → ném RuntimeException →
 * {@link com.hieu.common.outbox.OutboxDispatcher} giữ event để retry.
 *
 * <h3>Message format</h3>
 * <pre>
 * Topic  : money-bank.transfer-events       (configurable)
 * Key    : proposalId (aggregateId)          → ordering per aggregate, dedup key cho Inbox
 * Value  : JSON payload từ OutboxRecorder    → {"proposalId":"...", "status":"CONFIRMED", ...}
 * Headers:
 *   eventId        — UUID của OutboxEvent (idempotency key cho consumer Inbox)
 *   eventType      — "TransferConfirmed" | "TransferFailed"
 *   aggregateType  — "TRANSFER"
 *   schemaVersion  — "v1"
 *   occurredAt     — ISO-8601 UTC
 * </pre>
 *
 * <h3>Headers thay vì embedding vào value</h3>
 * <p>Giữ payload nguyên xi từ OutboxRecorder (không wrap). Metadata routing
 * (eventType, schemaVersion) đặt trong Kafka headers → consumer có thể route
 * theo header mà không cần deserialize value.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaOutboxMessagePublisher implements OutboxMessagePublisher {

    /** Timeout chờ broker ack. Nếu vượt quá → dispatcher retry. */
    private static final Duration SEND_TIMEOUT = Duration.ofSeconds(10);

    private final KafkaTemplate<String, String> outboxKafkaTemplate;
    private final OutboxKafkaProperties kafkaProperties;

    @Override
    public void publish(OutboxMessage message) {
        String topic  = resolveTopic(message);
        String key    = message.aggregateId();   // proposalId — partition key + Inbox dedup key
        String value  = message.payload();

        ProducerRecord<String, String> record = buildRecord(topic, key, value, message);

        log.debug("eventName=OUTBOX_PUBLISH_ATTEMPT eventId={} eventType={} topic={} key={}",
            message.eventId(), message.eventType(), topic, key);

        try {
            SendResult<String, String> result = outboxKafkaTemplate.send(record)
                .get(SEND_TIMEOUT.toSeconds(), TimeUnit.SECONDS);

            log.info("eventName=OUTBOX_PUBLISHED eventId={} eventType={} topic={} partition={} offset={}",
                message.eventId(), message.eventType(), topic,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());

        } catch (TimeoutException e) {
            // Broker không ack đúng hạn → dispatcher sẽ retry
            throw new KafkaSendException(
                "Kafka send timeout after " + SEND_TIMEOUT.toSeconds() + "s" +
                " [eventId=" + message.eventId() + "]", e);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaSendException(
                "Kafka send interrupted [eventId=" + message.eventId() + "]", e);

        } catch (ExecutionException e) {
            // Lỗi từ broker (topic không tồn tại, auth, v.v.)
            throw new KafkaSendException(
                "Kafka send failed [eventId=" + message.eventId() +
                " cause=" + e.getCause().getClass().getSimpleName() + "]",
                e.getCause());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Chọn topic theo aggregateType. Hiện tại chỉ có TRANSFER, sẵn sàng cho loại mới.
     */
    private String resolveTopic(OutboxMessage message) {
        return switch (message.aggregateType()) {
            case "TRANSFER" -> kafkaProperties.getTransferEventsTopic();
            default -> throw new KafkaSendException(
                "Unknown aggregateType for topic routing: " + message.aggregateType());
        };
    }

    /**
     * Dựng ProducerRecord với Kafka headers chứa metadata routing.
     *
     * <p>Headers cho phép consumer (notification-service) route event theo loại
     * mà không cần deserialize JSON body — đặc biệt hữu ích khi có nhiều loại
     * event trên cùng topic.
     */
    private ProducerRecord<String, String> buildRecord(
            String topic, String key, String value, OutboxMessage message) {

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

        record.headers()
            .add(new RecordHeader("eventId",
                message.eventId().getBytes(StandardCharsets.UTF_8)))
            .add(new RecordHeader("eventType",
                message.eventType().getBytes(StandardCharsets.UTF_8)))
            .add(new RecordHeader("aggregateType",
                message.aggregateType().getBytes(StandardCharsets.UTF_8)))
            .add(new RecordHeader("aggregateId",
                message.aggregateId().getBytes(StandardCharsets.UTF_8)))
            .add(new RecordHeader("schemaVersion",
                message.schemaVersion().getBytes(StandardCharsets.UTF_8)))
            .add(new RecordHeader("occurredAt",
                message.occurredAt().toString().getBytes(StandardCharsets.UTF_8)));

        return record;
    }

    /** RuntimeException để dispatcher phân biệt lỗi publish với lỗi khác. */
    public static class KafkaSendException extends RuntimeException {
        public KafkaSendException(String message) {
            super(message);
        }
        public KafkaSendException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
