package com.hieu.corebank.account.application.query;

import com.hieu.common.cqrs.QueryHandler;
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
 * V2: Đọc từ account_view (Read Side) thay vì bảng accounts (Write Side).
 */
@Profile({ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class GetAccountBalanceQueryHandler
        implements QueryHandler<GetAccountBalanceQuery, AccountBalanceResponseDTO> {

    private final AccountViewRepository accountViews;

    @Override
    @Transactional(readOnly = true)
    public AccountBalanceResponseDTO handle(GetAccountBalanceQuery query) {
        AccountView view = accountViews.findById(query.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found: " + query.accountId()));

        return AccountBalanceResponseDTO.builder()
                .accountId(view.getAccountId())
                .currency(view.getCurrency())
                .actualBalance(view.getActualBalance())
                .availableBalance(view.getAvailableBalance())
                .build();
    }
}
