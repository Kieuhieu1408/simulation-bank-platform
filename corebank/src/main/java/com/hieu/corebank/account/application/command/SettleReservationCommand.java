package com.hieu.corebank.account.application.command;

import com.hieu.common.cqrs.Command;
import com.hieu.corebank.shared.domain.AccountId;
import com.hieu.corebank.shared.domain.TransferId;

public record SettleReservationCommand(
        AccountId accountId,
        TransferId transferId
) implements Command<Void> {
}
