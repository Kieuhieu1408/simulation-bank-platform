package com.hieu.corebank.transfer.api.impl;

import com.hieu.common.constant.ActionType;
import com.hieu.corebank.access.security.CoreBankAuthorization;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.infrastructure.web.BaseController;
import com.hieu.corebank.transfer.api.TransferController;
import com.hieu.corebank.transfer.api.dto.request.TransferGetRequestDTO;
import com.hieu.corebank.transfer.api.dto.request.TransferHistoryRequestDTO;
import com.hieu.corebank.transfer.api.dto.request.TransferRequestDTO;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Profile({ProfileConstants.COMMAND, ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@RestController
public class TransferControllerImpl extends BaseController implements TransferController {

    @Override
    @CoreBankAuthorization(menuCode = "transfer_management", action = ActionType.CREATE)
    public ResponseEntity<TransferResponseDTO> transfer(TransferRequestDTO request) {
        return execute(request, TransferResponseDTO.class);
    }

    @Override
    @CoreBankAuthorization(menuCode = "transfer_management", action = ActionType.READ)
    public ResponseEntity<TransferResponseDTO> get(String id) {
        return executeQuery(new TransferGetRequestDTO(id), TransferResponseDTO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    @CoreBankAuthorization(menuCode = "transfer_management", action = ActionType.READ)
    public ResponseEntity<List<TransferResponseDTO>> history(String accountId) {
        return executeQuery(new TransferHistoryRequestDTO(accountId), (Class<List<TransferResponseDTO>>) (Class<?>) List.class);
    }
}
