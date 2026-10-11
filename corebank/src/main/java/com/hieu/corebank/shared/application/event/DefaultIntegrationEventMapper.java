package com.hieu.corebank.shared.application.event;

import com.hieu.corebank.account.domain.event.AccountCreatedEvent;
import com.hieu.corebank.shared.domain.DomainEvent;
import com.hieu.corebank.transfer.domain.event.TransferCompletedEvent;
import com.hieu.corebank.transfer.domain.event.TransferFailedEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * DefaultIntegrationEventMapper — Hiện thực chuyển đổi Domain Event sang Integration Event DTO (Phase 6).
 * Gán schemaVersion ổn định (1.0), chỉ publish các sự kiện đích thực của vòng đời nghiệp vụ.
 */
@Component
public class DefaultIntegrationEventMapper implements IntegrationEventMapper {

    @Override
    public Optional<IntegrationEvent> toIntegrationEvent(DomainEvent domainEvent) {
        if (domainEvent == null) {
            return Optional.empty();
        }

        if (domainEvent instanceof AccountCreatedEvent e) {
            return Optional.of(AccountOpenedEventDTO.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("AccountOpened")
                    .aggregateType("Account")
                    .aggregateId(e.aggregateId())
                    .schemaVersion("1.0")
                    .occurredAt(Instant.now())
                    .accountId(e.aggregateId())
                    .accountNumber(e.accountNumber())
                    .cifNumber(e.customerId())
                    .currency(e.currency())
                    .initialBalance(e.initialBalance())
                    .build());
        }

        if (domainEvent instanceof TransferCompletedEvent e) {
            return Optional.of(TransferCompletedEventDTO.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("TransferCompleted")
                    .aggregateType("Transfer")
                    .aggregateId(e.aggregateId())
                    .schemaVersion("1.0")
                    .occurredAt(Instant.now())
                    .transferId(e.aggregateId())
                    .sourceAccountId(e.sourceAccountId())
                    .destinationAccountId(e.destinationAccountId())
                    .amount(e.amount())
                    .currency(e.currency())
                    .status("COMPLETED")
                    .build());
        }

        if (domainEvent instanceof TransferFailedEvent e) {
            return Optional.of(TransferFailedEventDTO.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("TransferFailed")
                    .aggregateType("Transfer")
                    .aggregateId(e.aggregateId())
                    .schemaVersion("1.0")
                    .occurredAt(Instant.now())
                    .transferId(e.aggregateId())
                    .sourceAccountId(e.sourceAccountId())
                    .destinationAccountId(e.destinationAccountId())
                    .amount(e.amount())
                    .currency(e.currency())
                    .reason(e.reason())
                    .status("FAILED")
                    .build());
        }

        // Các intermediate steps như TransferInitiated, FundsReserved, TransferDestinationCredited -> Không đẩy ra integration event
        return Optional.empty();
    }
}
