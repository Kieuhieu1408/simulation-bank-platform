package com.hieu.corebank.account.application.query;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.account.api.dto.response.AccountBalanceResponseDTO;

public record GetAccountBalanceQuery(String accountId) implements Query<AccountBalanceResponseDTO> {
}
