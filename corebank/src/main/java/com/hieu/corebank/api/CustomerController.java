package com.hieu.corebank.api;

import com.hieu.corebank.dto.CustomerCreateRequestDTO;
import com.hieu.corebank.dto.AccountResponseDTO;
import com.hieu.corebank.dto.IssueCardResponseDTO;
import com.hieu.corebank.dto.CustomerResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/customers")
public interface CustomerController {

    @PostMapping
    ResponseEntity<CustomerResponseDTO> create(@Valid @RequestBody CustomerCreateRequestDTO request);

    @GetMapping("/{cifNumber}")
    ResponseEntity<CustomerResponseDTO> get(@PathVariable String cifNumber);

    @GetMapping("/{cifNumber}/accounts")
    ResponseEntity<List<AccountResponseDTO>> accounts(@PathVariable String cifNumber);

    @GetMapping("/{cifNumber}/cards")
    ResponseEntity<List<IssueCardResponseDTO>> cards(@PathVariable String cifNumber);
}