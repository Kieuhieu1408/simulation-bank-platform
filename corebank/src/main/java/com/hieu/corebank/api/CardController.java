package com.hieu.corebank.api;

import com.hieu.corebank.dto.response.CardResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/cards")
public interface CardController {

    @GetMapping("/{id}")
    ResponseEntity<CardResponseDTO> card(@PathVariable String id);
}