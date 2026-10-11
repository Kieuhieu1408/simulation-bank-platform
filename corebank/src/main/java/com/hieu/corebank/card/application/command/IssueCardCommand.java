package com.hieu.corebank.card.application.command;

import com.hieu.common.cqrs.Command;
import com.hieu.corebank.card.api.dto.response.IssueCardResponseDTO;

public record IssueCardCommand(String accountId) implements Command<IssueCardResponseDTO> {
}
