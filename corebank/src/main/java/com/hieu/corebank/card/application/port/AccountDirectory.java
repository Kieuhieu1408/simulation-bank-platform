package com.hieu.corebank.card.application.port;

import java.util.Optional;

/**
 * Port giao tiếp để Card Bounded Context tra cứu thông tin Tài khoản liên kết (Account).
 * Ngăn chặn Card Context phụ thuộc trực tiếp vào Account Infrastructure/Persistence.
 */
public interface AccountDirectory {

    Optional<AccountSummary> findById(String accountId);

    record AccountSummary(
            String accountId,
            String customerId,
            boolean active
    ) {}
}
