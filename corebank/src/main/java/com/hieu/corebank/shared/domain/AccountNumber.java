package com.hieu.corebank.shared.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object biểu diễn Số tài khoản ngân hàng (10-20 chữ số).
 */
public final class AccountNumber implements Serializable {

    private static final Pattern PATTERN = Pattern.compile("^[0-9]{10,20}$");

    private final String value;

    private AccountNumber(String value) {
        if (value == null) {
            throw new DomainException("AccountNumber cannot be null");
        }
        String trimmed = value.trim();
        if (!PATTERN.matcher(trimmed).matches()) {
            throw new DomainException("AccountNumber must be 10 to 20 digits: " + value);
        }
        this.value = trimmed;
    }

    public static AccountNumber of(String value) {
        return new AccountNumber(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountNumber that = (AccountNumber) o;
        return Objects.equals(value, that.value);
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
