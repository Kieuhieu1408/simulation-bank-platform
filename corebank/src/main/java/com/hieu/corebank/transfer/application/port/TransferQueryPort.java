package com.hieu.corebank.transfer.application.port;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Port thuộc tầng application của Transfer Bounded Context để đọc Read Model (transfer_view)
 * mà không phụ thuộc trực tiếp vào tầng infrastructure/persistence.
 */
public interface TransferQueryPort {

    record TransferReadModel(
            String transferId,
            String requestHash,
            String sourceAccountId,
            String destinationAccountId,
            BigDecimal amount,
            String currency,
            String description,
            String status,
            Instant createdAt,
            Instant updatedAt
    ) {}

    Optional<TransferReadModel> findById(String transferId);

    List<TransferReadModel> findHistoryByAccountId(String accountId);
}
