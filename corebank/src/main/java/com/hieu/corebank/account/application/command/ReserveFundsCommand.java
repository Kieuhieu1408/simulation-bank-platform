package com.hieu.corebank.account.application.command;

import com.hieu.common.cqrs.Command;
import com.hieu.corebank.shared.domain.AccountId;
import com.hieu.corebank.shared.domain.Money;
import com.hieu.corebank.shared.domain.TransferId;

public record ReserveFundsCommand(
        AccountId accountId,
        TransferId transferId,
        Money amount
) implements Command<Void> {
}
