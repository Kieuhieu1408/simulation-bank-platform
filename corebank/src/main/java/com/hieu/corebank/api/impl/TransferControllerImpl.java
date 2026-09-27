package com.hieu.corebank.api.impl;

import com.hieu.corebank.api.BaseController;
import com.hieu.corebank.api.TransferController;
import com.hieu.corebank.dto.request.TransferRequestDTO;
import com.hieu.corebank.dto.response.TransferResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferControllerImpl extends BaseController implements TransferController {

    @Override
    // @CoreBankAuthorise(menuCode = "Transfer", action = "Create")
    // @Backhut
    public ResponseEntity<TransferResponseDTO> transfer(TransferRequestDTO request) {
        return execute(request, TransferResponseDTO.class);
    }
}