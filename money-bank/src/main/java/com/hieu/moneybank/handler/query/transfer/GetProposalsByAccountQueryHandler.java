package com.hieu.moneybank.handler.query.transfer;

import com.hieu.common.cqrs.QueryHandler;
import com.hieu.common.security.IdentityContextResolver;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.request.TransferProposalHistoryRequestDTO;
import com.hieu.moneybank.dto.response.AccountResponseDTO;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import com.hieu.moneybank.repository.TransferProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetProposalsByAccountQueryHandler
        implements QueryHandler<TransferProposalHistoryRequestDTO, Page<TransferProposalResponseDTO>> {

    private final TransferProposalRepository repository;
    private final CorebankClient corebankClient;
    private final IdentityContextResolver identityContextResolver;

    @Override
    public Page<TransferProposalResponseDTO> handle(TransferProposalHistoryRequestDTO query) {
        String currentUserId = identityContextResolver.requireCurrent().customerId();
        AccountResponseDTO account = corebankClient.getAccount(query.getAccountId());

        if (!currentUserId.equals(account.getCifNumber())) {
            throw new AccessDeniedException("Bạn không có quyền xem lịch sử tài khoản này");
        }

        return repository.findByAccountId(
                query.getAccountId(), query.getFrom(), query.getTo(), query.getPageable()
        ).map(TransferProposalResponseDTO::from);
    }
}
