package com.hieu.moneybank.api;

import com.hieu.moneybank.dto.request.CustomerCreateRequestDTO;
import com.hieu.moneybank.dto.response.AccountResponseDTO;
import com.hieu.moneybank.dto.response.CardResponseDTO;
import com.hieu.moneybank.dto.response.CustomerResponseDTO;
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
    ResponseEntity<List<CardResponseDTO>> cards(@PathVariable String cifNumber);
}