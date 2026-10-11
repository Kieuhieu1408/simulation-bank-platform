package com.hieu.corebank.transfer.application.saga;

import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.TransferId;
import com.hieu.corebank.transfer.infrastructure.projection.TransferView;
import com.hieu.corebank.transfer.infrastructure.projection.TransferViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * SagaRecoveryJob — Quét và tự phục hồi các giao dịch chuyển tiền bị kẹt (Self-Healing - AC-CB-06, D15).
 *
 * <p>Hoạt động định kỳ để phát hiện các giao dịch ở trạng thái non-terminal
 * có {@code updated_at} vượt ngưỡng thời gian quy định (mặc định 30 giây).
 */
@Slf4j
@Profile({ProfileConstants.COMMAND, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class SagaRecoveryJob {

    private final TransferViewRepository transferViewRepository;
    private final TransferSaga transferSaga;
    private final SagaMetricsCollector metricsCollector;
    private final TransactionTemplate transactionTemplate;

    @Value("${corebank.transfer.stuck-threshold-seconds:30}")
    private long stuckThresholdSeconds;

    @Scheduled(fixedDelayString = "${corebank.transfer.recovery-interval-ms:10000}")
    public void recoverStuckTransfers() {
        Instant stuckThreshold = Instant.now().minus(stuckThresholdSeconds, ChronoUnit.SECONDS);
        // Transaction lấy lock FOR UPDATE SKIP LOCKED đồng thời 'lease' (touch updatedAt)
        // để ngăn pod khác chọn trùng bản ghi ngay khi lock kết thúc
        List<String> stuckTransferIds = transactionTemplate.execute(status -> {
            List<TransferView> stuckViews = transferViewRepository.findStuckTransfers(stuckThreshold);
            if (stuckViews.isEmpty()) {
                return List.of();
            }
            Instant now = Instant.now();
            for (TransferView view : stuckViews) {
                view.setUpdatedAt(now);
                transferViewRepository.save(view);
            }
            return stuckViews.stream().map(TransferView::getTransferId).toList();
        });

        if (stuckTransferIds == null || stuckTransferIds.isEmpty()) {
            metricsCollector.recordStuckTransfers(0);
            return;
        }

        log.warn("Found {} stuck transfer(s) needing recovery (threshold={}s)",
                stuckTransferIds.size(), stuckThresholdSeconds);
        metricsCollector.recordStuckTransfers(stuckTransferIds.size());

        for (String transferIdStr : stuckTransferIds) {
            try {
                log.info("Self-healing stuck transfer: transferId={}", transferIdStr);
                transferSaga.execute(TransferId.of(transferIdStr));
                log.info("Successfully recovered transfer: transferId={}", transferIdStr);
            } catch (Exception ex) {
                log.error("Failed to recover transferId={}: {}", transferIdStr, ex.getMessage(), ex);
            }
        }
    }
}
