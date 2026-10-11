package com.hieu.corebank.account.application.query;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;

import java.util.List;

public record GetAccountsByCifQuery(String cifNumber) implements Query<List<AccountResponseDTO>> {
}
