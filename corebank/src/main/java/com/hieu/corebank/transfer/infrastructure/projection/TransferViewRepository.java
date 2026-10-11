package com.hieu.corebank.transfer.infrastructure.projection;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TransferViewRepository extends JpaRepository<TransferView, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")})
    @Query("SELECT t FROM TransferView t WHERE t.status NOT IN ('COMPLETED', 'FAILED') AND t.updatedAt < :stuckThreshold")
    List<TransferView> findStuckTransfers(@Param("stuckThreshold") Instant stuckThreshold);

    List<TransferView> findBySourceAccountIdOrDestinationAccountIdOrderByCreatedAtDesc(
            String sourceAccountId, String destinationAccountId);
}
