package com.hieu.corebank.transfer.infrastructure.projection;

import com.hieu.corebank.transfer.application.port.StuckTransferQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StuckTransferQueryAdapter implements StuckTransferQueryPort {

    private final TransferViewRepository transferViewRepository;
    private final TransactionTemplate transactionTemplate;

    @Override
    public List<String> findAndLeaseStuckTransferIds(Instant stuckThreshold) {
        return transactionTemplate.execute(status -> {
            List<TransferView> stuckViews = transferViewRepository.findStuckTransfers(stuckThreshold);
            if (stuckViews.isEmpty()) {
                return List.of();
            }
            for (TransferView view : stuckViews) {
                view.setUpdatedAt(Instant.now());
                transferViewRepository.save(view);
            }
            return stuckViews.stream().map(TransferView::getTransferId).toList();
        });
    }
}
