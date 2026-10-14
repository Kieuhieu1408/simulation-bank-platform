package com.hieu.corebank.transfer.application.saga;

import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.TransferId;
import com.hieu.corebank.transfer.application.port.StuckTransferQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    private final StuckTransferQueryPort stuckTransferQueryPort;
    private final TransferSaga transferSaga;
    private final SagaMetricsCollector metricsCollector;

    @Value("${corebank.transfer.stuck-threshold-seconds:30}")
    private long stuckThresholdSeconds;

    @Scheduled(fixedDelayString = "${corebank.transfer.recovery-interval-ms:10000}")
    public void recoverStuckTransfers() {
        Instant stuckThreshold = Instant.now().minus(stuckThresholdSeconds, ChronoUnit.SECONDS);
        List<String> stuckTransferIds = stuckTransferQueryPort.findAndLeaseStuckTransferIds(stuckThreshold);

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
