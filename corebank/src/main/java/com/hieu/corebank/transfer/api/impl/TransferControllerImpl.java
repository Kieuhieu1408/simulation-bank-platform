package com.hieu.corebank.transfer.api.impl;

import com.hieu.common.constant.ActionType;
import com.hieu.corebank.access.security.CoreBankAuthorization;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.infrastructure.web.BaseController;
import com.hieu.corebank.transfer.api.TransferController;
import com.hieu.corebank.transfer.api.dto.request.TransferRequestDTO;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Profile({ProfileConstants.COMMAND, ProfileConstants.ALL, ProfileConstants.TEST})
@RestController
public class TransferControllerImpl extends BaseController implements TransferController {

    @Override
    @CoreBankAuthorization(menuCode = "transfer_management", action = ActionType.CREATE)
    public ResponseEntity<TransferResponseDTO> transfer(TransferRequestDTO request) {
        TransferResponseDTO response = dispatcher.dispatch(request.toCommand());
        if (response.getStatus() == com.hieu.common.constant.TransactionStatus.PENDING) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.ACCEPTED).body(response);
        }
        if (response.getStatus() == com.hieu.common.constant.TransactionStatus.FAILED) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        }
        return org.springframework.http.ResponseEntity.ok(response);
    }
}
