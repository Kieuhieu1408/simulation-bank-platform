package com.hieu.corebank.transfer.application.query;

import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.transfer.api.dto.request.TransferHistoryRequestDTO;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import com.hieu.corebank.transfer.application.port.TransferQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile({ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class GetTransferHistoryQueryHandler implements QueryHandler<TransferHistoryRequestDTO, List<TransferResponseDTO>> {

    private final TransferQueryPort transferQueryPort;

    @Override
    @Transactional(readOnly = true)
    public List<TransferResponseDTO> handle(TransferHistoryRequestDTO query) {
        return transferQueryPort
                .findHistoryByAccountId(query.getAccountId())
                .stream()
                .map(TransferResponseDTO::from)
                .toList();
    }
}
