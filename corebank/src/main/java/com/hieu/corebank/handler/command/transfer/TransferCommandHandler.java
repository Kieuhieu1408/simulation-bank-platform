package com.hieu.corebank.handler.command.transfer;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.dto.request.TransferRequestDTO;
import com.hieu.corebank.dto.response.TransferResponseDTO;
import com.hieu.corebank.eventsourcing.aggregate.AccountAggregate;
import com.hieu.corebank.eventsourcing.store.EventStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * TransferCommandHandler V2 — Event Sourcing edition.
 *
 * <p>Những thứ đã loại bỏ so với V1:
 * <ul>
 *   <li>❌ SELECT ... FOR UPDATE (Pessimistic Lock) — thay bằng Optimistic Lock trên DB</li>
 *   <li>❌ Sắp xếp ID để tránh Deadlock</li>
 *   <li>❌ account.debit() / account.credit() ghi đè balance column</li>
 *   <li>❌ Idempotency check thủ công qua findByIdempotencyKey</li>
 * </ul>
 *
 * <p>Những thứ mới trong V2:
 * <ul>
 *   <li>✅ AccountAggregate sống trên RAM — validate bằng availableBalance</li>
 *   <li>✅ FundsReservedEvent → TransferCompletedEvent → FundsDepositedEvent</li>
 *   <li>✅ EventStore.append() tự ghi Outbox trong cùng transaction</li>
 *   <li>✅ Retry tự động khi DataIntegrityViolationException (Optimistic Lock conflict)</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransferCommandHandler implements CommandHandler<TransferRequestDTO, TransferResponseDTO> {

    private static final int MAX_RETRIES = 3;

    private final EventStore eventStore;

    @Override
    public TransferResponseDTO handle(TransferRequestDTO request) {
        String transferId = UUID.randomUUID().toString();

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return doTransfer(request, transferId);
            } catch (DataIntegrityViolationException ex) {
                if (attempt == MAX_RETRIES) {
                    log.error("Transfer failed after {} retries due to concurrent conflict: transferId={}",
                            MAX_RETRIES, transferId);
                    throw ex;
                }
                log.warn("Optimistic lock conflict on attempt {}/{}, retrying transferId={}",
                        attempt, MAX_RETRIES, transferId);
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    @Transactional
    protected TransferResponseDTO doTransfer(TransferRequestDTO request, String transferId) {
        if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
            throw new com.hieu.corebank.exception.BusinessException(
                    "Source and destination accounts must be different");
        }

        String currency = request.getCurrency().toUpperCase();

        // 1. Load source aggregate từ Event Store (Snapshot + Replay trên RAM)
        AccountAggregate source = eventStore.load(request.getSourceAccountId());

        // 2. Validate và giữ tiền — KHÔNG có DB lock, validate trên RAM
        source.reserveFunds(request.getAmount(), currency, transferId);

        // 3. Load destination aggregate
        AccountAggregate destination = eventStore.load(request.getDestinationAccountId());

        // 4. Credit destination
        destination.depositFunds(request.getAmount(), currency, transferId);

        // 5. Confirm debit trên source
        source.completeTransfer(request.getAmount(), currency,
                transferId, request.getDestinationAccountId());

        // 6. Persist tất cả events + outbox trong cùng 1 DB transaction
        //    Nếu 2 thread cùng ghi version conflict → DataIntegrityViolationException → retry
        eventStore.append(source);
        eventStore.append(destination);

        log.info("Transfer completed: transferId={} source={} destination={} amount={} {}",
                transferId, request.getSourceAccountId(), request.getDestinationAccountId(),
                request.getAmount(), currency);

        return TransferResponseDTO.builder()
                .transactionId(transferId)
                .status(com.hieu.common.constant.TransactionStatus.SUCCESS)
                .sourceAccountId(request.getSourceAccountId())
                .destinationAccountId(request.getDestinationAccountId())
                .amount(request.getAmount())
                .currency(currency)
                .description(request.getDescription())
                .createdAt(java.time.Instant.now())
                .build();
    }
}