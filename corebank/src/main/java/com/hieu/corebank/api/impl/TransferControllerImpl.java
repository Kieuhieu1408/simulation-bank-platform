package com.hieu.corebank.api.impl;

import com.hieu.corebank.api.BaseController;
import com.hieu.corebank.api.TransferController;
import com.hieu.common.constant.ActionType;
import com.hieu.corebank.dto.request.TransferRequestDTO;
import com.hieu.corebank.dto.response.TransferResponseDTO;
import com.hieu.corebank.security.CoreBankAuthorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferControllerImpl extends BaseController implements TransferController {

    @Override
    @CoreBankAuthorization(menuCode = "transfer_management", action = ActionType.CREATE)
    public ResponseEntity<TransferResponseDTO> transfer(TransferRequestDTO request) {
        return execute(request, TransferResponseDTO.class);
    }
}