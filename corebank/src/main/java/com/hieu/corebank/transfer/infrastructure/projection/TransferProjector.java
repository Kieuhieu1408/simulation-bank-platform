package com.hieu.corebank.transfer.infrastructure.projection;

import com.hieu.corebank.transfer.domain.TransferStatus;
import com.hieu.corebank.transfer.domain.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;

/**
 * TransferProjector — đồng bộ hóa Read Side (bảng transfer_view) từ Domain Events của Transfer.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransferProjector {

    private final TransferViewRepository transferViewRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    @Transactional
    public void on(TransferInitiatedEvent event) {
        if (transferViewRepository.existsById(event.aggregateId())) {
            log.warn("TransferView already exists for transferId={}, skipping creation", event.aggregateId());
            return;
        }

        TransferView view = TransferView.builder()
                .transferId(event.aggregateId())
                .requestHash(event.requestHash())
                .sourceAccountId(event.sourceAccountId())
                .destinationAccountId(event.destinationAccountId())
                .amount(event.amount())
                .currency(event.currency())
                .description(event.description())
                .status(TransferStatus.INITIATED.name())
                .createdAt(event.occurredAt())
                .updatedAt(event.occurredAt())
                .build();

        transferViewRepository.save(view);
        log.debug("Projected TransferInitiated: transferId={}", event.aggregateId());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    @Transactional
    public void on(TransferFundsReservedEvent event) {
        updateStatus(event.aggregateId(), TransferStatus.SOURCE_RESERVED.name(), event.occurredAt());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    @Transactional
    public void on(TransferDestinationCreditedEvent event) {
        updateStatus(event.aggregateId(), TransferStatus.DESTINATION_CREDITED.name(), event.occurredAt());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    @Transactional
    public void on(TransferCompletedEvent event) {
        updateStatus(event.aggregateId(), TransferStatus.COMPLETED.name(), event.occurredAt());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    @Transactional
    public void on(TransferFailedEvent event) {
        updateStatus(event.aggregateId(), TransferStatus.FAILED.name(), event.occurredAt());
    }

    private void updateStatus(String transferId, String newStatus, Instant updatedAt) {
        transferViewRepository.findById(transferId).ifPresentOrElse(view -> {
            view.setStatus(newStatus);
            view.setUpdatedAt(updatedAt);
            transferViewRepository.save(view);
            log.debug("Projected status update: transferId={}, newStatus={}", transferId, newStatus);
        }, () -> log.warn("TransferView not found for transferId={}, cannot update status to {}", transferId, newStatus));
    }
}
