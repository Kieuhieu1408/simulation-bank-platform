package com.hieu.moneybank.repository;

import com.hieu.moneybank.constant.ProposalStatus;
import com.hieu.moneybank.domain.TransferProposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Optional;

@Repository
public interface TransferProposalRepository extends JpaRepository<TransferProposal, String> {

    Optional<TransferProposal> findByIdempotencyKey(String idempotencyKey);

    /**
     * Pessimistic write lock khi confirm proposal.
     * Chặn concurrent confirm cùng một proposal ID.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM TransferProposal p WHERE p.id = :id")
    Optional<TransferProposal> findByIdForUpdate(@Param("id") String id);

    /**
     * Lấy lịch sử proposals của account (source hoặc destination).
     */
    @Query("""
        SELECT p FROM TransferProposal p
        WHERE (p.sourceAccountId = :accountId OR p.destinationAccountId = :accountId)
          AND (:from IS NULL OR p.createdAt >= :from)
          AND (:to   IS NULL OR p.createdAt <= :to)
        ORDER BY p.createdAt DESC
        """)
    Page<TransferProposal> findByAccountId(
        @Param("accountId") String accountId,
        @Param("from") Instant from,
        @Param("to")   Instant to,
        Pageable pageable);

    /**
     * Đếm số proposal PENDING của một account nguồn — dùng để rate-limit
     * số lệnh chuyển chờ xác nhận (optional, phục vụ U-06 domain rule).
     */
    @Query("""
        SELECT COUNT(p) FROM TransferProposal p
        WHERE p.sourceAccountId = :accountId
          AND p.status = :status
        """)
    long countBySourceAccountIdAndStatus(
        @Param("accountId") String accountId,
        @Param("status")    ProposalStatus status);
}
