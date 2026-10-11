package com.hieu.corebank.transfer.application.saga;

import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.NotFoundException;
import com.hieu.corebank.shared.domain.TransferId;
import com.hieu.corebank.transfer.application.port.AccountGateway;
import com.hieu.corebank.transfer.domain.Transfer;
import com.hieu.corebank.transfer.domain.TransferRepository;
import com.hieu.corebank.transfer.domain.TransferStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * TransferSaga — Bộ điều phối Saga Orchestration cho quy trình Chuyển tiền.
 *
 * <p>Nguyên tắc thiết kế cốt lõi:
 * <ul>
 *   <li>Mỗi bước là 1 database transaction độc lập trên duy nhất 1 aggregate root.
 *   <li>Xác định rõ Điểm Pivot (Pivot Point) tại bước Ghi có tài khoản đích (tx4).
 *   <li>Trước Pivot: Nếu có lỗi -> Kích hoạt Compensating Transaction (Giải tỏa phong tỏa tài khoản nguồn).
 *   <li>Sau Pivot: Giao dịch đã thành công về bản chất -> Tiếp tục Settle tới cùng (retry / SagaRecoveryJob).
 *   <li>Mọi bước đều mang tính Idempotent theo transferId.
 * </ul>
 */
@Slf4j
@Profile({ProfileConstants.COMMAND, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class TransferSaga {

    private final TransferRepository transferRepository;
    private final AccountGateway accountGateway;
    private final SagaMetricsCollector metricsCollector;
    private final TransactionTemplate transactionTemplate;

    public TransferStatus execute(TransferId transferId) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new NotFoundException("Transfer not found: " + transferId.value()));

        if (transfer.status() == TransferStatus.COMPLETED || transfer.status() == TransferStatus.FAILED) {
            log.info("Transfer {} is already in terminal state: {}", transferId.value(), transfer.status());
            return transfer.status();
        }

        // ── Bước 1: Giữ tiền trên tài khoản nguồn (tx2) ─────────────────────────
        if (transfer.status() == TransferStatus.INITIATED) {
            boolean reserved = false;
            try {
                metricsCollector.recordStep("reserve", "attempt");
                accountGateway.reserveFunds(transfer.sourceAccountId(), transfer.id(), transfer.amount());
                metricsCollector.recordStep("reserve", "success");
                reserved = true;

                // Cập nhật trạng thái Transfer -> SOURCE_RESERVED (tx3)
                transactionTemplate.execute(status -> {
                    Transfer t = transferRepository.findById(transferId).orElseThrow();
                    t.markSourceReserved();
                    transferRepository.save(t);
                    return null;
                });
                transfer = transferRepository.findById(transferId).orElseThrow();
            } catch (Exception ex) {
                log.error("Saga Step 1 (Reserve) failed for transferId={}: {}", transferId.value(), ex.getMessage());
                metricsCollector.recordStep("reserve", "failure");
                if (reserved) {
                    try {
                        accountGateway.releaseReservation(transfer.sourceAccountId(), transfer.id(),
                                "Compensating release: failed to record SOURCE_RESERVED (" + ex.getMessage() + ")");
                    } catch (Exception releaseEx) {
                        log.error("Compensating release after step 1 status failure failed for transferId={}: {}",
                                transferId.value(), releaseEx.getMessage());
                    }
                }
                failTransfer(transferId, "Reserve rejected: " + ex.getMessage());
                throw ex;
            }
        }

        // ── Bước 2: Ghi có tài khoản đích (tx4 - ĐIỂM PIVOT) ─────────────────────
        if (transfer.status() == TransferStatus.SOURCE_RESERVED) {
            boolean credited = false;
            try {
                metricsCollector.recordStep("credit", "attempt");
                accountGateway.creditFunds(transfer.destinationAccountId(), transfer.id(), transfer.amount());
                metricsCollector.recordStep("credit", "success");
                credited = true;

                // Cập nhật trạng thái Transfer -> DESTINATION_CREDITED (tx5)
                transactionTemplate.execute(status -> {
                    Transfer t = transferRepository.findById(transferId).orElseThrow();
                    t.markDestinationCredited();
                    transferRepository.save(t);
                    return null;
                });
                transfer = transferRepository.findById(transferId).orElseThrow();
            } catch (Exception ex) {
                if (credited) {
                    // ĐIỂM PIVOT ĐÃ VƯỢT QUA: Tài khoản đích ĐÃ nhận được tiền!
                    // Tuyệt đối KHÔNG release tài khoản nguồn và KHÔNG đánh dấu FAILED (tránh tạo tiền khống).
                    log.error("Saga Step 2: Destination credited but failed to record DESTINATION_CREDITED for transferId={}. "
                            + "Post-pivot state, DO NOT COMPENSATE. Will be recovered by SagaRecoveryJob.",
                            transferId.value(), ex);
                    metricsCollector.recordStep("credit_status_update", "failure");
                    throw ex;
                }

                log.error("Saga Step 2 (Credit) failed before pivot for transferId={}: {}. Executing compensating release...",
                        transferId.value(), ex.getMessage());
                metricsCollector.recordStep("credit", "failure");

                // Compensating Transaction: Giải tỏa phong tỏa trên tài khoản nguồn
                boolean releaseSuccess = false;
                try {
                    metricsCollector.recordStep("release", "attempt");
                    accountGateway.releaseReservation(transfer.sourceAccountId(), transfer.id(),
                            "Compensating release: destination credit failed (" + ex.getMessage() + ")");
                    metricsCollector.recordStep("release", "success");
                    releaseSuccess = true;
                } catch (Exception releaseEx) {
                    log.error("Compensating release failed for transferId={}: {}", transferId.value(), releaseEx.getMessage());
                    metricsCollector.recordStep("release", "failure");
                }

                if (releaseSuccess) {
                    failTransfer(transferId, "Destination credit failed: " + ex.getMessage());
                } else {
                    log.warn("Transfer {} left in SOURCE_RESERVED state so SagaRecoveryJob can retry release",
                            transferId.value());
                }
                throw ex;
            }
        }

        // ── Bước 3: Quyết toán trên tài khoản nguồn (tx6 - SAU PIVOT) ─────────────
        if (transfer.status() == TransferStatus.DESTINATION_CREDITED) {
            try {
                metricsCollector.recordStep("settle", "attempt");
                accountGateway.settleReservation(transfer.sourceAccountId(), transfer.id());
                metricsCollector.recordStep("settle", "success");

                // Hoàn tất Transfer -> COMPLETED (tx7)
                transactionTemplate.execute(status -> {
                    Transfer t = transferRepository.findById(transferId).orElseThrow();
                    t.complete();
                    transferRepository.save(t);
                    return null;
                });
                metricsCollector.recordStep("complete", "success");
            } catch (Exception ex) {
                log.error("Saga Step 3 (Settle) failed after pivot for transferId={}: {}. Stuck for recovery job.",
                        transferId.value(), ex.getMessage());
                metricsCollector.recordStep("settle", "failure");
                throw ex;
            }
        }

        return transferRepository.findById(transferId).map(Transfer::status).orElse(TransferStatus.FAILED);
    }

    private void failTransfer(TransferId transferId, String reason) {
        try {
            transactionTemplate.execute(status -> {
                Transfer t = transferRepository.findById(transferId).orElseThrow();
                t.fail(reason);
                transferRepository.save(t);
                return null;
            });
            metricsCollector.recordStep("fail", "success");
        } catch (Exception ex) {
            log.error("Failed to mark transfer as failed: transferId={}, error={}", transferId.value(), ex.getMessage());
            metricsCollector.recordStep("fail", "failure");
        }
    }
}
