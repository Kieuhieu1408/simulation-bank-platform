package com.hieu.corebank.card.application.query;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.card.api.dto.response.IssueCardResponseDTO;

import java.util.List;

public record GetCardsByCifQuery(String cifNumber) implements Query<List<IssueCardResponseDTO>> {
}
