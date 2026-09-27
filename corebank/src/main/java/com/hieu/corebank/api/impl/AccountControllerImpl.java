package com.hieu.corebank.api.impl;

import com.hieu.corebank.api.AccountController;
import com.hieu.corebank.api.BaseController;
import com.hieu.corebank.dto.*;
import com.hieu.corebank.handler.query.account.GetAccountBalanceQueryHandler.GetAccountBalanceQuery;
import com.hieu.corebank.handler.query.account.GetAccountByIdQueryHandler.GetAccountByIdQuery;
import com.hieu.corebank.handler.query.transfer.GetTransferHistoryQueryHandler.GetTransferHistoryQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class AccountControllerImpl extends BaseController implements AccountController {

    @Override
    public ResponseEntity<AccountResponseDTO> create(AccountCreateRequestDTO request) {
        return execute(request, AccountResponseDTO.class);
    }

    @Override
    public ResponseEntity<AccountResponseDTO> get(String id) {
        return executeQuery(new GetAccountByIdQuery(id), AccountResponseDTO.class);
    }

    @Override
    public ResponseEntity<AccountBalanceResponseDTO> balance(String id) {
        return executeQuery(new GetAccountBalanceQuery(id), AccountBalanceResponseDTO.class);
    }

    @Override
    public ResponseEntity<IssueCardResponseDTO> issueCard(String id) {
        return execute(new IssueCardRequestDTO(id), IssueCardResponseDTO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<Page<TransactionHistoryItemDTO>> history(
            String id, Instant from, Instant to, int page, int size) {
        return executeQuery(
                new GetTransferHistoryQuery(id, from, to,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "occurredAt"))),
                (Class<Page<TransactionHistoryItemDTO>>) (Class<?>) Page.class
        );
    }
}