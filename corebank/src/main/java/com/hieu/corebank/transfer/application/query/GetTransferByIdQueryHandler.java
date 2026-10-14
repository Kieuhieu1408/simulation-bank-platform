package com.hieu.corebank.transfer.application.query;

import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.NotFoundException;
import com.hieu.corebank.transfer.api.dto.request.TransferGetRequestDTO;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import com.hieu.corebank.transfer.infrastructure.projection.TransferView;
import com.hieu.corebank.transfer.infrastructure.projection.TransferViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile({ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class GetTransferByIdQueryHandler implements QueryHandler<TransferGetRequestDTO, TransferResponseDTO> {

    private final TransferViewRepository transferViewRepository;

    @Override
    @Transactional(readOnly = true)
    public TransferResponseDTO handle(TransferGetRequestDTO query) {
        TransferView view = transferViewRepository.findById(query.getTransferId())
                .orElseThrow(() -> new NotFoundException("Transfer not found: " + query.getTransferId()));
        return TransferResponseDTO.from(view);
    }
}
