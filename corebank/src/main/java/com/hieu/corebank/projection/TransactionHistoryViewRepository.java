package com.hieu.corebank.projection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface TransactionHistoryViewRepository extends JpaRepository<TransactionHistoryView, String> {

    @Query("""
        SELECT t FROM TransactionHistoryView t
        WHERE t.accountId = :accountId
          AND t.occurredAt >= :from
          AND t.occurredAt <= :to
        ORDER BY t.occurredAt DESC
        """)
    Page<TransactionHistoryView> findByAccountId(
            @Param("accountId") String accountId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);
}