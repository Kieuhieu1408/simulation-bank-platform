package com.hieu.corebank.account.api.impl;

import com.hieu.common.constant.ActionType;
import com.hieu.corebank.access.security.CoreBankAuthorization;
import com.hieu.corebank.account.api.AccountController;
import com.hieu.corebank.account.api.dto.request.AccountCreateRequestDTO;
import com.hieu.corebank.account.api.dto.response.AccountBalanceResponseDTO;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.api.dto.response.TransactionHistoryItemDTO;
import com.hieu.corebank.account.application.query.GetAccountBalanceQuery;
import com.hieu.corebank.account.application.query.GetAccountByIdQuery;
import com.hieu.corebank.account.application.query.GetTransactionHistoryQuery;
import com.hieu.corebank.card.api.dto.response.IssueCardResponseDTO;
import com.hieu.corebank.card.application.command.IssueCardCommand;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.infrastructure.web.BaseController;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Profile({ProfileConstants.COMMAND, ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@RestController
public class AccountControllerImpl extends BaseController implements AccountController {

    @Override
    @CoreBankAuthorization(menuCode = "account_management", action = ActionType.CREATE)
    public ResponseEntity<AccountResponseDTO> create(AccountCreateRequestDTO request) {
        return execute(request.toCommand(), AccountResponseDTO.class);
    }

    @Override
    @CoreBankAuthorization(menuCode = "account_management", action = ActionType.READ)
    public ResponseEntity<AccountResponseDTO> get(String id) {
        return executeQuery(new GetAccountByIdQuery(id), AccountResponseDTO.class);
    }

    @Override
    @CoreBankAuthorization(menuCode = "account_management", action = ActionType.READ)
    public ResponseEntity<AccountBalanceResponseDTO> balance(String id) {
        return executeQuery(new GetAccountBalanceQuery(id), AccountBalanceResponseDTO.class);
    }

    @Override
    @CoreBankAuthorization(menuCode = "card_management", action = ActionType.CREATE)
    public ResponseEntity<IssueCardResponseDTO> issueCard(String id) {
        return execute(new IssueCardCommand(id), IssueCardResponseDTO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    @CoreBankAuthorization(menuCode = "account_management", action = ActionType.READ)
    public ResponseEntity<Page<TransactionHistoryItemDTO>> history(
            String id, Instant from, Instant to, int page, int size) {
        return executeQuery(
                new GetTransactionHistoryQuery(id, from, to,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "occurredAt"))),
                (Class<Page<TransactionHistoryItemDTO>>) (Class<?>) Page.class
        );
    }
}
