package com.hieu.moneybank.api;

import com.hieu.moneybank.dto.request.TransferRequestDTO;
import com.hieu.moneybank.dto.response.TransferResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/transfers")
public interface TransferController {

    @PostMapping
    ResponseEntity<TransferResponseDTO> transfer(@Valid @RequestBody TransferRequestDTO request);

}