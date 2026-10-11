package com.hieu.corebank.shared.application.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * TransferFailedEventDTO — Integration Event khi chuyển tiền thất bại.
 * Đẩy qua Outbox để Kafka Dispatcher gửi tới topic bank.transfers.events.
 * schemaVersion: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferFailedEventDTO implements IntegrationEvent {

    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    @Builder.Default
    private String eventType = "TransferFailed";

    @Builder.Default
    private String aggregateType = "Transfer";

    private String aggregateId;

    @Builder.Default
    private String schemaVersion = "1.0";

    @Builder.Default
    private Instant occurredAt = Instant.now();

    private String transferId;
    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;
    private String currency;
    private String reason;

    @Builder.Default
    private String status = "FAILED";
}
