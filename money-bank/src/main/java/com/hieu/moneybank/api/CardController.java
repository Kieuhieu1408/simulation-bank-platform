package com.hieu.moneybank.api;

import com.hieu.moneybank.dto.response.IssueCardResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/cards")
public interface CardController {

    @GetMapping("/{id}")
    ResponseEntity<IssueCardResponseDTO> card(@PathVariable String id);
}