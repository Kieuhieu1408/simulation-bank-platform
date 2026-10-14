package com.hieu.corebank.transfer.api;

import com.hieu.corebank.transfer.api.dto.request.TransferRequestDTO;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/transfers")
public interface TransferController {

    @PostMapping
    ResponseEntity<TransferResponseDTO> transfer(@Valid @RequestBody TransferRequestDTO request);

    @GetMapping("/{id}")
    ResponseEntity<TransferResponseDTO> get(@PathVariable String id);

    @GetMapping
    ResponseEntity<List<TransferResponseDTO>> history(@RequestParam String accountId);
}
