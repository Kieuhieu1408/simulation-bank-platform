package com.hieu.moneybank.handler.query.transfer;

import com.hieu.common.cqrs.QueryHandler;
import com.hieu.common.security.IdentityContextResolver;
import com.hieu.moneybank.dto.request.TransferProposalGetRequestDTO;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import com.hieu.moneybank.exception.NotFoundException;
import com.hieu.moneybank.repository.TransferProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetProposalByIdQueryHandler
        implements QueryHandler<TransferProposalGetRequestDTO, TransferProposalResponseDTO> {

    private final TransferProposalRepository repository;
    private final IdentityContextResolver identityContextResolver;

    @Override
    public TransferProposalResponseDTO handle(TransferProposalGetRequestDTO query) {
        var entity = repository.findById(query.getProposalId())
                .orElseThrow(() -> new NotFoundException("Proposal không tồn tại: " + query.getProposalId()));

        String currentUserId = identityContextResolver.requireCurrent().customerId();
        if (!currentUserId.equals(entity.getInitiatorCustomerId())) {
            throw new AccessDeniedException("Bạn không có quyền xem proposal này");
        }

        return TransferProposalResponseDTO.from(entity);
    }
}
