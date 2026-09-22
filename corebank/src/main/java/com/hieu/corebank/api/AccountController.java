package com.hieu.corebank.api;

import com.hieu.corebank.dto.AccountCreateRequestDTO;
import com.hieu.corebank.dto.AccountBalanceResponseDTO;
import com.hieu.corebank.dto.AccountResponseDTO;
import com.hieu.corebank.dto.IssueCardResponseDTO;
import com.hieu.corebank.dto.TransferResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RequestMapping("/api/v1/accounts")
public interface AccountController {

    @PostMapping
    ResponseEntity<AccountResponseDTO> create(@Valid @RequestBody AccountCreateRequestDTO request);

    @GetMapping("/{id}")
    ResponseEntity<AccountResponseDTO> get(@PathVariable String id);

    @GetMapping("/{id}/balance")
    ResponseEntity<AccountBalanceResponseDTO> balance(@PathVariable String id);

    @PostMapping("/{id}/cards")
    ResponseEntity<IssueCardResponseDTO> issueCard(@PathVariable String id);

    @GetMapping("/{id}/transactions")
    ResponseEntity<Page<TransferResponseDTO>> history(
            @PathVariable String id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);
}