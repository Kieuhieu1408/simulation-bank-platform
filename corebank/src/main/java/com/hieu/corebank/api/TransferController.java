package com.hieu.corebank.api;

import com.hieu.corebank.api.dto.TransferDtos;
import com.hieu.corebank.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {
    private final TransferService transfers;
    public TransferController(TransferService transfers) { this.transfers = transfers; }
    @PostMapping
    public ResponseEntity<TransferDtos.Response> transfer(@Valid @RequestBody TransferDtos.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(TransferDtos.Response.from(transfers.transfer(request)));
    }
}
