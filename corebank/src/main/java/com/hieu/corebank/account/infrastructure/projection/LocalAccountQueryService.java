package com.hieu.corebank.account.infrastructure.projection;

import com.hieu.common.constant.AccountStatus;
import com.hieu.corebank.account.application.port.AccountQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Adapter hiện thực AccountQueryService port bằng cách truy vấn AccountViewRepository.
 */
@Service
@RequiredArgsConstructor
public class LocalAccountQueryService implements AccountQueryService {

    private final AccountViewRepository accountViewRepository;

    @Override
    public Optional<AccountSummaryDto> findSummaryById(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            return Optional.empty();
        }
        return accountViewRepository.findById(accountId)
                .map(v -> new AccountSummaryDto(
                        v.getAccountId(),
                        v.getCustomerId(),
                        v.getStatus() == AccountStatus.ACTIVE
                ));
    }
}
