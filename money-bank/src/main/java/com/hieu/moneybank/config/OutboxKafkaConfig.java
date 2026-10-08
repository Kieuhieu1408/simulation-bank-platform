package com.hieu.moneybank.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka producer factory cho Outbox publisher (U-07).
 */
@Configuration
public class OutboxKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, String> outboxProducerFactory(
            OutboxKafkaProperties outboxKafkaProperties) {

        Map<String, Object> props = new HashMap<>();

        if (bootstrapServers != null && !bootstrapServers.isBlank()) {
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        }

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

        // Delivery timeout = send-timeout của dispatcher: sau khi dispatcher coi lần
        // thử là lỗi, producer không được tiếp tục gửi ngầm. Kafka yêu cầu
        // delivery.timeout.ms >= linger.ms + request.timeout.ms.
        long sendTimeoutMs = outboxKafkaProperties.getSendTimeout().toMillis();
        if (sendTimeoutMs < outboxKafkaProperties.getRequestTimeoutMs()) {
            throw new IllegalStateException(
                    "money-bank.kafka.send-timeout (" + sendTimeoutMs + "ms) phải >= request-timeout-ms ("
                            + outboxKafkaProperties.getRequestTimeoutMs() + "ms)");
        }
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, String.valueOf(sendTimeoutMs));

        // Không buffer thêm: OutboxDispatcher tự điều tiết
        props.put(ProducerConfig.LINGER_MS_CONFIG, "0");

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> outboxKafkaTemplate(
            ProducerFactory<String, String> outboxProducerFactory) {
        return new KafkaTemplate<>(outboxProducerFactory);
    }
}
