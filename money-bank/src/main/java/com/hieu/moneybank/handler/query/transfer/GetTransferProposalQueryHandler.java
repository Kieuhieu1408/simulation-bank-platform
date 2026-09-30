package com.hieu.moneybank.handler.query.transfer;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import com.hieu.moneybank.exception.NotFoundException;
import com.hieu.moneybank.repository.TransferProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Query handlers cho Transfer Proposal.
 */
@Slf4j
public class GetTransferProposalQueryHandler {

    // ──────────────────────────────────────────────────────────────────────────
    // Query: Lấy một proposal theo ID
    // ──────────────────────────────────────────────────────────────────────────

    public record GetProposalByIdQuery(String proposalId) implements Query<TransferProposalResponseDTO> {}

    @Component
    @RequiredArgsConstructor
    public static class GetProposalByIdQueryHandler
            implements QueryHandler<GetProposalByIdQuery, TransferProposalResponseDTO> {

        private final TransferProposalRepository repository;

        @Override
        public TransferProposalResponseDTO handle(GetProposalByIdQuery query) {
            return repository.findById(query.proposalId())
                .map(TransferProposalResponseDTO::from)
                .orElseThrow(() -> new NotFoundException("Proposal không tồn tại: " + query.proposalId()));
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Query: Lấy lịch sử proposals theo account
    // ──────────────────────────────────────────────────────────────────────────

    public record GetProposalsByAccountQuery(
        String accountId,
        Instant from,
        Instant to,
        Pageable pageable
    ) implements Query<Page<TransferProposalResponseDTO>> {}

    @Component
    @RequiredArgsConstructor
    public static class GetProposalsByAccountQueryHandler
            implements QueryHandler<GetProposalsByAccountQuery, Page<TransferProposalResponseDTO>> {

        private final TransferProposalRepository repository;

        @Override
        public Page<TransferProposalResponseDTO> handle(GetProposalsByAccountQuery query) {
            return repository.findByAccountId(
                query.accountId(), query.from(), query.to(), query.pageable()
            ).map(TransferProposalResponseDTO::from);
        }
    }
}
