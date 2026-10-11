package com.hieu.corebank.card.infrastructure.account;

import com.hieu.corebank.account.application.port.AccountQueryService;
import com.hieu.corebank.card.application.port.AccountDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter hiện thực AccountDirectory port bằng cách tra cứu qua AccountQueryService của account.application.
 * Đặt tại tầng infrastructure của Card để bảo đảm application của Card chỉ phụ thuộc vào Port nội bộ,
 * đồng thời không bị phụ thuộc trực tiếp vào tầng infrastructure hay entity của Account.
 */
@Component
@RequiredArgsConstructor
public class LocalAccountDirectory implements AccountDirectory {

    private final AccountQueryService accountQueryService;

    @Override
    public Optional<AccountSummary> findById(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            return Optional.empty();
        }
        return accountQueryService.findSummaryById(accountId)
                .map(s -> new AccountSummary(
                        s.accountId(),
                        s.customerId(),
                        s.active()
                ));
    }
}
