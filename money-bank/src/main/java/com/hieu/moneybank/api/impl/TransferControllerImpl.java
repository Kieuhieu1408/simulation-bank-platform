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
    public ResponseEntity<TransferResponseDTO> transfer(TransferRequestDTO request, String idempotencyKeyHeader) {
        if ((request.getIdempotencyKey() == null || request.getIdempotencyKey().isBlank())
                && idempotencyKeyHeader != null && !idempotencyKeyHeader.isBlank()) {
            request.setIdempotencyKey(idempotencyKeyHeader);
        }
        return execute(request, TransferResponseDTO.class);
    }
}
