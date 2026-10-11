package com.hieu.corebank.card.api;

import com.hieu.corebank.card.api.dto.request.IssueCardRequestDTO;
import com.hieu.corebank.card.api.dto.response.IssueCardResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/cards")
public interface CardController {

    @PostMapping
    ResponseEntity<IssueCardResponseDTO> issue(@Valid @RequestBody IssueCardRequestDTO request);

    @GetMapping("/{id}")
    ResponseEntity<IssueCardResponseDTO> card(@PathVariable String id);
}
