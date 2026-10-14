package com.hieu.corebank.card.application.command;

import com.hieu.corebank.card.api.dto.request.IssueCardRequestDTO;

public class IssueCardCommand extends IssueCardRequestDTO {
    public IssueCardCommand(String accountId) {
        super(accountId);
    }

    public String accountId() {
        return getAccountId();
    }
}
