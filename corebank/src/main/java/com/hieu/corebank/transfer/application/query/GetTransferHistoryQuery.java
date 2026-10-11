package com.hieu.corebank.transfer.application.query;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;

import java.util.List;

public record GetTransferHistoryQuery(String accountId) implements Query<List<TransferResponseDTO>> {
}
