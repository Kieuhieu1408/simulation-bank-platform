package com.hieu.corebank.shared.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object định danh Tài khoản (AccountId).
 */
public final class AccountId implements Serializable {

    private final String value;

    private AccountId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("AccountId cannot be null or blank");
        }
        this.value = value.trim();
    }

    public static AccountId of(String value) {
        return new AccountId(value);
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID().toString());
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountId accountId = (AccountId) o;
        return Objects.equals(value, accountId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
