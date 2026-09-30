package com.hieu.moneybank.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Cấu hình Kafka cho Outbox publisher (U-07).
 *
 * <p>Tất cả topic name và tuning knob tập trung ở đây, không hardcode trong code.
 *
 * <h3>Topic contract (OQ-P1-01)</h3>
 * <ul>
 *   <li>{@code transfer-events} — topic chính cho TransferConfirmed / TransferFailed</li>
 * </ul>
 *
 * <h3>Partition key</h3>
 * <p>Key = {@code aggregateId} (proposalId) → các event của cùng một proposal
 * được đảm bảo thứ tự trên cùng partition. Consumer Inbox dùng key để dedup.
 *
 * <h3>Acks</h3>
 * <p>{@code acks=all} (replica-ack) + {@code enable.idempotence=true} để không mất
 * event khi broker failover. Cấu hình này làm chậm throughput nhưng đây là flow
 * tài chính, độ bền ưu tiên hơn tốc độ.
 */
@Component
@ConfigurationProperties(prefix = "money-bank.kafka")
public class OutboxKafkaProperties {

    /** Topic nhận tất cả sự kiện chuyển tiền (TransferConfirmed, TransferFailed). */
    private String transferEventsTopic = "transfer-events";

    /**
     * Số ms chờ leader+replicas xác nhận trước khi coi là lỗi.
     * 0 = fire-and-forget (không dùng), 5000 = đủ dài cho most deployments.
     */
    private int requestTimeoutMs = 5000;

    /** Số lần retry nội bộ của KafkaTemplate khi broker tạm thời không respond. */
    private int retries = 3;

    public String getTransferEventsTopic() {
        return transferEventsTopic;
    }

    public void setTransferEventsTopic(String transferEventsTopic) {
        this.transferEventsTopic = transferEventsTopic;
    }

    public int getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(int requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }
}
