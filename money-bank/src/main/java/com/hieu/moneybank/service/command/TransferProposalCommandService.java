package com.hieu.moneybank.service.command;

import com.hieu.moneybank.domain.TransferProposal;

import java.util.Optional;

/**
 * Service layer cho write operations trên TransferProposal.
 *
 * <p>Các phương thức này được gọi từ handler trong transaction của handler.
 * Không có logic nghiệp vụ ở đây — chỉ là data access wrapper.
 */
public interface TransferProposalCommandService {

    /**
     * Lưu proposal (insert hoặc update).
     */
    TransferProposal save(TransferProposal proposal);

    /**
     * Tìm proposal với pessimistic write lock để confirm.
     */
    Optional<TransferProposal> findByIdForUpdate(String id);

    /**
     * Tìm proposal theo idempotency key (cho idempotency check).
     */
    Optional<TransferProposal> findByIdempotencyKey(String idempotencyKey);
}
