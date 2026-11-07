package com.hieu.corebank.transfer.application.command;

import com.hieu.common.constant.TransactionStatus;
import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.*;
import com.hieu.corebank.transfer.api.dto.request.TransferRequestDTO;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import com.hieu.corebank.transfer.application.port.TransferIdempotencyLockPort;
import com.hieu.corebank.transfer.application.port.TransferIdempotencyLockPort.LockHandle;
import com.hieu.corebank.transfer.application.port.TransferIdempotencyLockPort.LockResult;
import com.hieu.corebank.transfer.application.port.TransferQueryPort;
import com.hieu.corebank.transfer.application.port.TransferQueryPort.TransferReadModel;
import com.hieu.corebank.transfer.application.saga.TransferSaga;
import com.hieu.corebank.transfer.domain.Transfer;
import com.hieu.corebank.transfer.domain.TransferRepository;
import com.hieu.corebank.transfer.domain.TransferStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
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
import java.util.concurrent.*;

/**
 * TransferHandler — Use Case xử lý khởi tạo chuyển tiền (Thẩm định Cấp độ 2).
 *
 * <p>Quy trình thẩm định và xử lý:
 * <ul>
 *   <li>1. Sinh mã transferId tất định: {@code UUID v3(sourceAccountId + ":" + idempotencyKey)}.
 *   <li>2. Tính requestHash và đối soát Idempotency trên Read Model:
 *       Khác hash -> Ném 409 Conflict (409001); Đã hoàn tất + khớp hash -> Replay (<= 50ms).
 *   <li>3. Thẩm định Invariant: Nguồn != Đích; Số tiền > 0.
 *   <li>4. Cấp khóa phân tán In-Flight trên Redis (Cách 1): Nếu đang được Pod khác xử lý -> Ném 409 Conflict (409002).
 *   <li>5. Khởi tạo Transfer Aggregate Root trong transaction riêng (tx1).
 *   <li>6. Điều phối Saga với Bounded Latency (D1) và chỉ xóa Redis Lock khi luồng Saga thực sự kết thúc.
 * </ul>
 */
@Slf4j
@Profile({ProfileConstants.COMMAND, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class TransferHandler implements CommandHandler<TransferRequestDTO, TransferResponseDTO> {

    private final TransferRepository transferRepository;
    private final TransferQueryPort transferQueryPort;
    private final TransferIdempotencyLockPort idempotencyLockPort;
    private final TransferSaga transferSaga;
    private final TransactionTemplate transactionTemplate;
    private final ExecutorService transferSagaExecutor;

    @Value("${corebank.transfer.sync-timeout-seconds}")
    private long syncTimeoutSeconds;

    @Override
    public TransferResponseDTO handle(TransferRequestDTO command) {
        String idempotencyKey = command.getIdempotencyKey();
        String transferIdStr = UUID.nameUUIDFromBytes(
                (command.getSourceAccountId() + ":" + idempotencyKey).getBytes(StandardCharsets.UTF_8)
        ).toString();
        TransferId transferId = TransferId.of(transferIdStr);
        String requestHash = computeRequestHash(command);

        // 1. Thẩm định Cấp độ 2: Kiểm tra nhanh tại Read Model (transfer_view)
        //    - Lệch requestHash -> Ném ngay 409001 (ConflictException)
        //    - Đã ở trạng thái kết thúc (COMPLETED / FAILED) và khớp hash -> Replay ngay không cần chạm Redis
        Optional<TransferReadModel> existingView = transferQueryPort.findById(transferIdStr);
        if (existingView.isPresent()) {
            TransferReadModel view = existingView.get();
            verifyRequestHash(view.requestHash(), requestHash, idempotencyKey, transferIdStr);
            if (isTerminalStatus(view.status())) {
                log.info("Idempotent replay for transferId={}", transferIdStr);
                return TransferResponseDTO.from(view);
            }
        }

        // 2. Thẩm định Cấp độ 2: Kiểm tra tài khoản nguồn và đích phải khác nhau
        if (command.getSourceAccountId().equals(command.getDestinationAccountId())) {
            throw new BusinessException("Source and destination accounts must be different");
        }

        // 3. Xin khóa In-Flight trên Redis để chống 2 Pod xử lý đồng thời cùng 1 giao dịch
        LockResult lockResult = idempotencyLockPort.tryAcquire(transferId, requestHash);
        LockHandle lockHandle = switch (lockResult) {
            case LockResult.Acquired acquired -> acquired.handle();
            case LockResult.AlreadyInFlight inFlight -> {
                if (inFlight.existingRequestHash() != null) {
                    verifyRequestHash(inFlight.existingRequestHash(), requestHash, idempotencyKey, transferIdStr);
                }
                log.warn("Concurrent in-flight transfer rejected for key={} transferId={}", idempotencyKey, transferIdStr);
                throw new InProgressConflictException("Transfer with this idempotency key is currently being processed");
            }
            case LockResult.LockUnavailable ignored -> () -> {};
        };

        boolean handoverToAsyncSaga = false;
        try {
            // Nếu trước khi lấy được lock đã có bản ghi non-terminal trong transfer_view
            // (ví dụ Pod trước vừa chạy xong và nhả lock, hoặc giao dịch cũ đang chờ SagaRecoveryJob)
            if (existingView.isPresent()) {
                Optional<TransferReadModel> refreshedView = transferQueryPort.findById(transferIdStr);
                if (refreshedView.isPresent()) {
                    TransferReadModel view = refreshedView.get();
                    verifyRequestHash(view.requestHash(), requestHash, idempotencyKey, transferIdStr);
                    log.info("Idempotent replay after acquiring lock for transferId={}, status={}",
                            transferIdStr, view.status());
                    return TransferResponseDTO.from(view);
                }
            }

            // 4. Khởi tạo Transfer Aggregate Root (tx1)
            // Chốt chặn Cấp độ 4 (Database Constraint) bảo vệ tuyệt đối kể cả khi Redis tạm thời không khả dụng
            boolean isNewTransfer;
            try {
                isNewTransfer = Boolean.TRUE.equals(transactionTemplate.execute(status -> {
                    Optional<Transfer> existing = transferRepository.findById(transferId);
                    if (existing.isEmpty()) {
                        Transfer transfer = Transfer.initiate(
                                transferId,
                                AccountId.of(command.getSourceAccountId()),
                                AccountId.of(command.getDestinationAccountId()),
                                Money.of(command.getAmount(), Currency.of(command.getCurrency())),
                                command.getDescription(),
                                requestHash
                        );
                        transferRepository.save(transfer);
                        return true;
                    }
                    return false;
                }));
            } catch (DataIntegrityViolationException ex) {
                // Xử lý race condition tại DB khi nhiều request cùng idempotencyKey đến lúc Redis fallback
                Optional<TransferReadModel> concurrentView = transferQueryPort.findById(transferIdStr);
                if (concurrentView.isPresent()) {
                    TransferReadModel view = concurrentView.get();
                    verifyRequestHash(view.requestHash(), requestHash, idempotencyKey, transferIdStr);
                    log.info("Concurrent request committed, replaying transferId={}", transferIdStr);
                    return TransferResponseDTO.from(view);
                }
                throw ex;
            }

            // Nếu request đồng thời đã khởi tạo transfer, đối soát hash và không chạy saga song song
            if (!isNewTransfer) {
                log.info("Transfer already initiated by concurrent request: transferId={}", transferIdStr);
                Optional<TransferReadModel> view = transferQueryPort.findById(transferIdStr);
                if (view.isPresent()) {
                    verifyRequestHash(view.get().requestHash(), requestHash, idempotencyKey, transferIdStr);
                    return TransferResponseDTO.from(view.get());
                }
                throw new InProgressConflictException("Transfer with this idempotency key is currently being processed");
            }

            // 5. Điều phối Saga với Bounded Latency (D1)
            // Giữ khóa Redis cho đến khi luồng Saga thực sự kết thúc (kể cả khi quá syncTimeoutSeconds chuyển sang chạy ngầm)
            CompletableFuture<TransferStatus> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return transferSaga.execute(transferId);
                } finally {
                    lockHandle.release();
                }
            }, transferSagaExecutor);
            handoverToAsyncSaga = true;

            try {
                future.get(syncTimeoutSeconds, TimeUnit.SECONDS);
                TransferReadModel view = transferQueryPort.findById(transferIdStr).orElseThrow();
                return TransferResponseDTO.from(view);
            } catch (TimeoutException ex) {
                log.warn("Transfer {} timed out after {}s, returning 202 PENDING (Saga will complete in background)",
                        transferIdStr, syncTimeoutSeconds);
                TransferReadModel view = transferQueryPort.findById(transferIdStr).orElse(null);
                if (view != null) {
                    TransferResponseDTO dto = TransferResponseDTO.from(view);
                    dto.setStatus(TransactionStatus.PENDING);
                    return dto;
                }
                return TransferResponseDTO.builder()
                        .transactionId(transferIdStr)
                        .status(TransactionStatus.PENDING)
                        .sourceAccountId(command.getSourceAccountId())
                        .destinationAccountId(command.getDestinationAccountId())
                        .amount(command.getAmount())
                        .currency(command.getCurrency().toUpperCase())
                        .description(command.getDescription())
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
        } finally {
            if (!handoverToAsyncSaga) {
                lockHandle.release();
            }
        }
    }

    private void verifyRequestHash(String storedHash, String incomingHash, String idempotencyKey, String transferIdStr) {
        if (!storedHash.equals(incomingHash)) {
            log.warn("Idempotency conflict for key={} transferId={}: request payload mismatch",
                    idempotencyKey, transferIdStr);
            throw new ConflictException("Idempotency key conflict: request payload differs from previous request");
        }
    }

    private static boolean isTerminalStatus(String rawStatus) {
        return TransferStatus.COMPLETED.name().equalsIgnoreCase(rawStatus)
                || TransferStatus.FAILED.name().equalsIgnoreCase(rawStatus)
                || "SUCCESS".equalsIgnoreCase(rawStatus);
    }

    private String computeRequestHash(TransferRequestDTO command) {
        String raw = command.getSourceAccountId() + ":"
                + command.getDestinationAccountId() + ":"
                + command.getAmount().stripTrailingZeros().toPlainString() + ":"
                + command.getCurrency().toUpperCase();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
