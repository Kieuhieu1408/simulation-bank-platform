package com.hieu.corebank.handler.command.transfer;

import com.hieu.common.constant.TransactionStatus;
import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.dto.request.TransferRequestDTO;
import com.hieu.corebank.dto.response.TransferResponseDTO;
import com.hieu.corebank.eventsourcing.aggregate.AccountAggregate;
import com.hieu.corebank.eventsourcing.store.EventStore;
import com.hieu.corebank.exception.BusinessException;
import com.hieu.corebank.exception.ConflictException;
import com.hieu.corebank.projection.TransferView;
import com.hieu.corebank.projection.TransferViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

/**
 * TransferCommandHandler — Điều phối chuyển tiền và đảm bảo Idempotency độc lập tại Corebank.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransferCommandHandler implements CommandHandler<TransferRequestDTO, TransferResponseDTO> {

    private static final int MAX_RETRIES = 3;

    private final EventStore             eventStore;
    private final TransferViewRepository transferViewRepository;
    private final TransactionTemplate    transactionTemplate;

    @Override
    public TransferResponseDTO handle(TransferRequestDTO request) {
        String idempotencyKey = request.getIdempotencyKey();
        String transferId = UUID.nameUUIDFromBytes(
                (request.getSourceAccountId() + ":" + idempotencyKey).getBytes(StandardCharsets.UTF_8)
        ).toString();
        String requestHash = computeRequestHash(request);

        // 1. Kiểm tra Idempotency tại Corebank trước khi xử lý
        Optional<TransferView> existingView = transferViewRepository.findById(transferId);
        if (existingView.isPresent()) {
            TransferView view = existingView.get();
            if (view.getRequestHash().equals(requestHash)) {
                log.info("Idempotent replay for transferId={}", transferId);
                return TransferResponseDTO.from(view);
            } else {
                log.warn("Idempotency conflict for key={} (transferId={}): payload mismatch",
                        idempotencyKey, transferId);
                throw new ConflictException("Idempotency key conflict: request payload differs from previous request");
            }
        }

        // 2. Thực hiện transfer trong transaction với cơ chế retry khi có xung đột đồng thời
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return transactionTemplate.execute(status -> doTransfer(request, transferId, requestHash));
            } catch (DataIntegrityViolationException ex) {
                // Kiểm tra nếu transaction vừa được commit bởi concurrent request
                Optional<TransferView> concurrentView = transferViewRepository.findById(transferId);
                if (concurrentView.isPresent()) {
                    TransferView view = concurrentView.get();
                    if (view.getRequestHash().equals(requestHash)) {
                        log.info("Concurrent request already committed, replaying transferId={}", transferId);
                        return TransferResponseDTO.from(view);
                    } else {
                        throw new ConflictException("Idempotency key conflict: request payload differs from previous request");
                    }
                }

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

    protected TransferResponseDTO doTransfer(TransferRequestDTO request, String transferId, String requestHash) {
        if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
            throw new BusinessException("Source and destination accounts must be different");
        }

        String currency = request.getCurrency().toUpperCase();

        // 1. Load source aggregate từ Event Store
        AccountAggregate source = eventStore.load(request.getSourceAccountId());

        // 2. Validate và giữ tiền trên RAM
        source.reserveFunds(request.getAmount(), currency, transferId);

        // 3. Load destination aggregate
        AccountAggregate destination = eventStore.load(request.getDestinationAccountId());

        // 4. Credit destination
        destination.depositFunds(request.getAmount(), currency, transferId);

        // 5. Confirm debit trên source
        source.completeTransfer(request.getAmount(), currency,
                transferId, request.getDestinationAccountId());

        // 6. Persist tất cả events + outbox trong cùng 1 DB transaction
        eventStore.append(source);
        eventStore.append(destination);

        // 7. Ghi nhận transfer_view phục vụ Idempotency và Query
        Instant now = Instant.now();
        TransferView transferView = TransferView.builder()
                .transferId(transferId)
                .requestHash(requestHash)
                .sourceAccountId(request.getSourceAccountId())
                .destinationAccountId(request.getDestinationAccountId())
                .amount(request.getAmount())
                .currency(currency)
                .description(request.getDescription())
                .status(TransactionStatus.SUCCESS.name())
                .createdAt(now)
                .updatedAt(now)
                .build();
        transferViewRepository.save(transferView);

        log.info("Transfer completed: transferId={} source={} destination={} amount={} {}",
                transferId, request.getSourceAccountId(), request.getDestinationAccountId(),
                request.getAmount(), currency);

        return TransferResponseDTO.builder()
                .transactionId(transferId)
                .status(TransactionStatus.SUCCESS)
                .sourceAccountId(request.getSourceAccountId())
                .destinationAccountId(request.getDestinationAccountId())
                .amount(request.getAmount())
                .currency(currency)
                .description(request.getDescription())
                .createdAt(now)
                .build();
    }

    private String computeRequestHash(TransferRequestDTO request) {
        String raw = request.getSourceAccountId() + ":"
                + request.getDestinationAccountId() + ":"
                + request.getAmount().stripTrailingZeros().toPlainString() + ":"
                + request.getCurrency().toUpperCase();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}