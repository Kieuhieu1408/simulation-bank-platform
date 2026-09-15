package com.hieu.moneybank.api.impl;

import com.hieu.moneybank.api.BaseController;
import com.hieu.moneybank.api.TransferController;
import com.hieu.moneybank.dto.request.TransferRequestDTO;
import com.hieu.moneybank.dto.response.TransferResponseDTO;
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