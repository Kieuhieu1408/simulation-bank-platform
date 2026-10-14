package com.hieu.corebank.account.domain;

/**
 * Domain Port cho lưu trữ và truy xuất AccountAggregate.
 */
public interface AccountRepository {

    AccountAggregate load(String accountId);

    void save(AccountAggregate account);
}
