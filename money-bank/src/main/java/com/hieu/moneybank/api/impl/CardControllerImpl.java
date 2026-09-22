package com.hieu.moneybank.api.impl;

import com.hieu.moneybank.api.BaseController;
import com.hieu.moneybank.api.CardController;
import com.hieu.moneybank.dto.response.IssueCardResponseDTO;
import com.hieu.moneybank.handler.query.card.GetCardByIdQueryHandler.GetCardByIdQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardControllerImpl extends BaseController implements CardController {

    @Override
    public ResponseEntity<IssueCardResponseDTO> card(String id) {
        return executeQuery(new GetCardByIdQuery(id), IssueCardResponseDTO.class);
    }
}