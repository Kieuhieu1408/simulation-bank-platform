package com.hieu.corebank.account.application.query;

import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.account.api.dto.request.AccountBalanceRequestDTO;
import com.hieu.corebank.account.api.dto.response.AccountBalanceResponseDTO;
import com.hieu.corebank.account.infrastructure.projection.AccountView;
import com.hieu.corebank.account.infrastructure.projection.AccountViewRepository;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * GetAccountBalanceQueryHandler — Truy vấn số dư tài khoản từ account_view (Read Side).
 */
@Profile({ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class GetAccountBalanceQueryHandler
        implements QueryHandler<AccountBalanceRequestDTO, AccountBalanceResponseDTO> {

    private final AccountViewRepository accountViews;

    @Override
    @Transactional(readOnly = true)
    public AccountBalanceResponseDTO handle(AccountBalanceRequestDTO query) {
        AccountView view = accountViews.findById(query.getAccountId())
                .orElseThrow(() -> new NotFoundException("Account not found: " + query.getAccountId()));

        return AccountBalanceResponseDTO.builder()
                .accountId(view.getAccountId())
                .currency(view.getCurrency())
                .actualBalance(view.getActualBalance())
                .availableBalance(view.getAvailableBalance())
                .build();
    }
}
