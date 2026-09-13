package com.hieu.corebank.api.impl;

import com.hieu.corebank.api.BaseController;
import com.hieu.corebank.api.CardController;
import com.hieu.corebank.dto.response.CardResponseDTO;
import com.hieu.corebank.handler.query.card.GetCardByIdQueryHandler.GetCardByIdQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardControllerImpl extends BaseController implements CardController {

    @Override
    public ResponseEntity<CardResponseDTO> card(String id) {
        return executeQuery(new GetCardByIdQuery(id), CardResponseDTO.class);
    }
}