package com.hieu.corebank.shared.application.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * AccountOpenedEventDTO — Integration Event khi một tài khoản thanh toán được mở thành công.
 * schemaVersion: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountOpenedEventDTO implements IntegrationEvent {

    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    @Builder.Default
    private String eventType = "AccountOpened";

    @Builder.Default
    private String aggregateType = "Account";

    private String aggregateId;

    @Builder.Default
    private String schemaVersion = "1.0";

    @Builder.Default
    private Instant occurredAt = Instant.now();

    private String accountId;
    private String accountNumber;
    private String cifNumber;
    private String currency;
    private BigDecimal initialBalance;
}
