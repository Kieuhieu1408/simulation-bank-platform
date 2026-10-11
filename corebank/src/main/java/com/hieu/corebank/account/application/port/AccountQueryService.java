package com.hieu.corebank.account.application.port;

import java.util.Optional;

/**
 * Port công khai thuộc tầng application của Bounded Context Account.
 * Cho phép các Bounded Context khác (như Card) truy vấn thông tin tóm tắt của tài khoản
 * mà không phải phụ thuộc trực tiếp vào tầng domain hoặc infrastructure của Account.
 */
public interface AccountQueryService {

    record AccountSummaryDto(String accountId, String customerId, boolean active) {}

    Optional<AccountSummaryDto> findSummaryById(String accountId);
}
