package com.hieu.moneybank.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka producer factory cho Outbox publisher (U-07).
 *
 * <h3>Thiết kế</h3>
 * <ul>
 *   <li>Key = String (proposalId) — đảm bảo ordering per aggregate.</li>
 *   <li>Value = String (JSON payload) — giữ encoding đơn giản, không dùng Avro vì
 *       Schema Registry chưa được chốt (OQ-P1-01). Khi chốt, đổi serializer ở đây.</li>
 *   <li>{@code acks=all} + {@code enable.idempotence=true} — không mất event khi
 *       broker failover. Phù hợp với flow tài chính.</li>
 *   <li>{@code linger.ms=0} — Outbox Dispatcher đã tự điều tiết tốc độ, không cần
 *       batching thêm tại producer.</li>
 * </ul>
 *
 * <p>Bean này được tạo chỉ khi {@code spring.kafka.bootstrap-servers} được cấu hình
 * (Spring Boot auto-config sẽ không khởi động Kafka client nếu thiếu bootstrap-servers).
 * Khi Kafka chưa available, {@code OutboxDispatcher} cũng tự tắt vì thiếu
 * {@link com.hieu.common.outbox.OutboxMessagePublisher} bean.
 */
@Configuration
public class OutboxKafkaConfig {

    /**
     * Producer factory với cấu hình tối ưu cho reliability.
     *
     * <p>Override một phần cấu hình từ {@link KafkaProperties} Spring Boot auto-config:
     * chỉ cần set acks và idempotence, bootstrap-servers lấy từ yaml.
     */
    @Bean
    public ProducerFactory<String, String> outboxProducerFactory(
            KafkaProperties kafkaProperties,
            OutboxKafkaProperties outboxKafkaProperties) {

        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties(null));

        // Key + Value serializer
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,   StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Reliability: broker phải confirm tất cả ISR replicas trước khi ack
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        // Idempotent producer: chống duplicate khi retry sau network partition
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        // Max outstanding requests phải <= 5 khi idempotence=true
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        // Retry trong producer — lớp retry ngoài là OutboxDispatcher
        props.put(ProducerConfig.RETRIES_CONFIG,
                String.valueOf(outboxKafkaProperties.getRetries()));

        // Request timeout
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,
                String.valueOf(outboxKafkaProperties.getRequestTimeoutMs()));

        // Không buffer thêm: OutboxDispatcher tự điều tiết
        props.put(ProducerConfig.LINGER_MS_CONFIG, "0");

        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * KafkaTemplate cho Outbox publisher — inject vào
     * {@link com.hieu.moneybank.messaging.KafkaOutboxMessagePublisher}.
     */
    @Bean
    public KafkaTemplate<String, String> outboxKafkaTemplate(
            ProducerFactory<String, String> outboxProducerFactory) {
        return new KafkaTemplate<>(outboxProducerFactory);
    }
}
