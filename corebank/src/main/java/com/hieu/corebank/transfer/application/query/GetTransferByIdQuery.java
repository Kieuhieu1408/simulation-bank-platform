package com.hieu.corebank.transfer.application.query;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;

public record GetTransferByIdQuery(String transferId) implements Query<TransferResponseDTO> {
}
