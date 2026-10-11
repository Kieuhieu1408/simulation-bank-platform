package com.hieu.corebank.account.application.command;

import com.hieu.common.cqrs.Command;
import com.hieu.corebank.shared.domain.AccountId;
import com.hieu.corebank.shared.domain.TransferId;

public record ReleaseReservationCommand(
        AccountId accountId,
        TransferId transferId,
        String reason
) implements Command<Void> {
}
