package com.hieu.corebank.transfer.infrastructure.projection;

import com.hieu.corebank.transfer.application.port.TransferQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransferQueryAdapter implements TransferQueryPort {

    private final TransferViewRepository transferViewRepository;

    @Override
    public Optional<TransferReadModel> findById(String transferId) {
        if (transferId == null || transferId.isBlank()) {
            return Optional.empty();
        }
        return transferViewRepository.findById(transferId)
                .map(this::toReadModel);
    }

    @Override
    public List<TransferReadModel> findHistoryByAccountId(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            return List.of();
        }
        return transferViewRepository
                .findBySourceAccountIdOrDestinationAccountIdOrderByCreatedAtDesc(accountId, accountId)
                .stream()
                .map(this::toReadModel)
                .toList();
    }

    private TransferReadModel toReadModel(TransferView v) {
        return new TransferReadModel(
                v.getTransferId(),
                v.getRequestHash(),
                v.getSourceAccountId(),
                v.getDestinationAccountId(),
                v.getAmount(),
                v.getCurrency(),
                v.getDescription(),
                v.getStatus(),
                v.getCreatedAt(),
                v.getUpdatedAt()
        );
    }
}
