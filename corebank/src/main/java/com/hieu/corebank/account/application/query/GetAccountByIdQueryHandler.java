package com.hieu.corebank.account.application.query;

import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.account.api.dto.request.AccountGetRequestDTO;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.infrastructure.projection.AccountView;
import com.hieu.corebank.account.infrastructure.projection.AccountViewRepository;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * GetAccountByIdQueryHandler — Truy vấn thông tin tài khoản theo accountId.
 */
@Profile({ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class GetAccountByIdQueryHandler implements QueryHandler<AccountGetRequestDTO, AccountResponseDTO> {

    private final AccountViewRepository accounts;

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDTO handle(AccountGetRequestDTO query) {
        AccountView account = accounts.findById(query.getAccountId())
                .orElseThrow(() -> new NotFoundException("Account not found: " + query.getAccountId()));
        return AccountResponseDTO.from(account);
    }
}
