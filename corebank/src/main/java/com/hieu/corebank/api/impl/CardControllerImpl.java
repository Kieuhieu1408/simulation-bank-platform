package com.hieu.corebank.api.impl;

import com.hieu.corebank.api.BaseController;
import com.hieu.corebank.api.CardController;
import com.hieu.corebank.constant.ActionType;
import com.hieu.corebank.dto.response.IssueCardResponseDTO;
import com.hieu.corebank.handler.query.card.GetCardByIdQueryHandler.GetCardByIdQuery;
import com.hieu.corebank.security.CoreBankAuthorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardControllerImpl extends BaseController implements CardController {

    @Override
    @CoreBankAuthorization(menuCode = "card_management", action = ActionType.READ)
    public ResponseEntity<IssueCardResponseDTO> card(String id) {
        return executeQuery(new GetCardByIdQuery(id), IssueCardResponseDTO.class);
    }
}