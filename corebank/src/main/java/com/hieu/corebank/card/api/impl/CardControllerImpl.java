package com.hieu.corebank.card.api.impl;

import com.hieu.common.constant.ActionType;
import com.hieu.corebank.access.security.CoreBankAuthorization;
import com.hieu.corebank.card.api.CardController;
import com.hieu.corebank.card.api.dto.request.IssueCardRequestDTO;
import com.hieu.corebank.card.api.dto.response.IssueCardResponseDTO;
import com.hieu.corebank.card.application.query.GetCardByIdQuery;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.infrastructure.web.BaseController;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Profile({ProfileConstants.COMMAND, ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@RestController
public class CardControllerImpl extends BaseController implements CardController {

    @Override
    @CoreBankAuthorization(menuCode = "card_management", action = ActionType.CREATE)
    public ResponseEntity<IssueCardResponseDTO> issue(IssueCardRequestDTO request) {
        return execute(request.toCommand(), IssueCardResponseDTO.class);
    }

    @Override
    @CoreBankAuthorization(menuCode = "card_management", action = ActionType.READ)
    public ResponseEntity<IssueCardResponseDTO> card(String id) {
        return executeQuery(new GetCardByIdQuery(id), IssueCardResponseDTO.class);
    }
}
