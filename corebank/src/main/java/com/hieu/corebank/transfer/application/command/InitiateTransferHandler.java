package com.hieu.corebank.transfer.application.command;

import com.hieu.common.constant.TransactionStatus;
import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.*;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import com.hieu.corebank.transfer.application.saga.TransferSaga;
import com.hieu.corebank.transfer.domain.Transfer;
import com.hieu.corebank.transfer.domain.TransferRepository;
import com.hieu.corebank.transfer.domain.TransferStatus;
import com.hieu.corebank.transfer.infrastructure.projection.TransferView;
import com.hieu.corebank.transfer.infrastructure.projection.TransferViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * InitiateTransferHandler — Use Case xử lý khởi tạo chuyển tiền (Thẩm định Cấp độ 2).
 *
 * <p>Quy trình thẩm định và xử lý:
 * <ul>
 *   <li>1. Sinh mã transferId tất định: {@code UUID v3(sourceAccountId + ":" + idempotencyKey)}.
 *   <li>2. Tính requestHash và đối soát Idempotency: Khớp -> Replay (<= 50ms); Khác -> Ném 409 Conflict.
 *   <li>3. Thẩm định Invariant: Nguồn != Đích; Số tiền > 0.
 *   <li>4. Khởi tạo Transfer Aggregate Root trong transaction riêng (tx1).
 *   <li>5. Điều phối Saga với Bounded Latency (D1): Chờ tối đa N giây, quá hạn fallback 202 PENDING.
 * </ul>
 */
@Slf4j
@Profile({ProfileConstants.COMMAND, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class InitiateTransferHandler implements CommandHandler<InitiateTransferCommand, TransferResponseDTO>, DisposableBean {

    private final TransferRepository transferRepository;
    private final TransferViewRepository transferViewRepository;
    private final TransferSaga transferSaga;
    private final TransactionTemplate transactionTemplate;

    private final ExecutorService sagaExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @Value("${corebank.transfer.sync-timeout-seconds:3}")
    private long syncTimeoutSeconds;

    @Override
    public TransferResponseDTO handle(InitiateTransferCommand command) {
        String idempotencyKey = command.idempotencyKey();
        String transferIdStr = UUID.nameUUIDFromBytes(
                (command.sourceAccountId() + ":" + idempotencyKey).getBytes(StandardCharsets.UTF_8)
        ).toString();
        TransferId transferId = TransferId.of(transferIdStr);
        String requestHash = computeRequestHash(command);

        // 1. Thẩm định Cấp độ 2: Kiểm tra Idempotency tại Corebank
        Optional<TransferView> existingView = transferViewRepository.findById(transferIdStr);
        if (existingView.isPresent()) {
            TransferView view = existingView.get();
            if (view.getRequestHash().equals(requestHash)) {
                log.info("Idempotent replay for transferId={}", transferIdStr);
                return TransferResponseDTO.from(view);
            } else {
                log.warn("Idempotency conflict for key={} transferId={}: request payload mismatch",
                        idempotencyKey, transferIdStr);
                throw new ConflictException("Idempotency key conflict: request payload differs from previous request");
            }
        }

        // 2. Thẩm định Cấp độ 2: Kiểm tra tài khoản nguồn và đích phải khác nhau
        if (command.sourceAccountId().equals(command.destinationAccountId())) {
            throw new BusinessException("Source and destination accounts must be different");
        }

        // 3. Khởi tạo Transfer Aggregate Root (tx1)
        boolean isNewTransfer;
        try {
            isNewTransfer = Boolean.TRUE.equals(transactionTemplate.execute(status -> {
                Optional<Transfer> existing = transferRepository.findById(transferId);
                if (existing.isEmpty()) {
                    Transfer transfer = Transfer.initiate(
                            transferId,
                            AccountId.of(command.sourceAccountId()),
                            AccountId.of(command.destinationAccountId()),
                            Money.of(command.amount(), Currency.of(command.currency())),
                            command.description(),
                            requestHash
                    );
                    transferRepository.save(transfer);
                    return true;
                }
                return false;
            }));
        } catch (DataIntegrityViolationException ex) {
            // Xử lý race condition khi nhiều request cùng idempotencyKey đến cùng lúc
            Optional<TransferView> concurrentView = transferViewRepository.findById(transferIdStr);
            if (concurrentView.isPresent()) {
                TransferView view = concurrentView.get();
                if (view.getRequestHash().equals(requestHash)) {
                    log.info("Concurrent request committed, replaying transferId={}", transferIdStr);
                    return TransferResponseDTO.from(view);
                } else {
                    throw new ConflictException("Idempotency key conflict: request payload differs from previous request");
                }
            }
            throw ex;
        }

        // Nếu request đồng thời đã khởi tạo transfer, không chạy saga song song để tránh tranh chấp version
        if (!isNewTransfer) {
            log.info("Transfer already initiated by concurrent request: transferId={}", transferIdStr);
            Optional<TransferView> view = transferViewRepository.findById(transferIdStr);
            if (view.isPresent()) {
                return TransferResponseDTO.from(view.get());
            }
        }

        // 4. Điều phối Saga với Bounded Latency (D1)
        CompletableFuture<TransferStatus> future = CompletableFuture.supplyAsync(
                () -> transferSaga.execute(transferId),
                sagaExecutor
        );

        try {
            TransferStatus status = future.get(syncTimeoutSeconds, TimeUnit.SECONDS);
            TransferView view = transferViewRepository.findById(transferIdStr).orElseThrow();
            return TransferResponseDTO.from(view);
        } catch (TimeoutException ex) {
            log.warn("Transfer {} timed out after {}s, returning 202 PENDING (Saga will complete in background)",
                    transferIdStr, syncTimeoutSeconds);
            TransferView view = transferViewRepository.findById(transferIdStr).orElse(null);
            if (view != null) {
                TransferResponseDTO dto = TransferResponseDTO.from(view);
                dto.setStatus(TransactionStatus.PENDING);
                return dto;
            }
            return TransferResponseDTO.builder()
                    .transactionId(transferIdStr)
                    .status(TransactionStatus.PENDING)
                    .sourceAccountId(command.sourceAccountId())
                    .destinationAccountId(command.destinationAccountId())
                    .amount(command.amount())
                    .currency(command.currency().toUpperCase())
                    .description(command.description())
                    .createdAt(Instant.now())
                    .build();
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException re) {
                throw re;
            }
            throw new RuntimeException(cause);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Transfer execution interrupted", ie);
        }
    }

    private String computeRequestHash(InitiateTransferCommand command) {
        String raw = command.sourceAccountId() + ":"
                + command.destinationAccountId() + ":"
                + command.amount().stripTrailingZeros().toPlainString() + ":"
                + command.currency().toUpperCase();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    @Override
    public void destroy() {
        log.info("Shutting down sagaExecutor in InitiateTransferHandler...");
        sagaExecutor.shutdown();
        try {
            if (!sagaExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                sagaExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            sagaExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
