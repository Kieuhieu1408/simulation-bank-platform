package com.hieu.corebank.customer.api;

import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.card.api.dto.response.IssueCardResponseDTO;
import com.hieu.corebank.customer.api.dto.request.CustomerCreateRequestDTO;
import com.hieu.corebank.customer.api.dto.response.CustomerResponseDTO;
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
