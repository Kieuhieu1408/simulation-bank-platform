package com.hieu.corebank.card.application.query;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.card.api.dto.response.IssueCardResponseDTO;

public record GetCardByIdQuery(String cardId) implements Query<IssueCardResponseDTO> {
}
