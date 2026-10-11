package com.hieu.corebank.transfer.application.command;

import com.hieu.common.cqrs.Command;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InitiateTransferCommand(
        String sourceAccountId,
        String destinationAccountId,
        BigDecimal amount,
        String currency,
        String idempotencyKey,
        String description
) implements Command<TransferResponseDTO> {
}
