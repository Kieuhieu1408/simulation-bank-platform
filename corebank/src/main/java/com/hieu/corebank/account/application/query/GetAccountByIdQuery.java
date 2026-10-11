package com.hieu.corebank.account.application.query;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;

public record GetAccountByIdQuery(String accountId) implements Query<AccountResponseDTO> {
}
