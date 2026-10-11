package com.hieu.corebank.account.application.command;

import com.hieu.common.cqrs.Command;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;

import java.math.BigDecimal;

public record CreateAccountCommand(
        String cifNumber,
        String currency,
        BigDecimal initialBalance
) implements Command<AccountResponseDTO> {
}
