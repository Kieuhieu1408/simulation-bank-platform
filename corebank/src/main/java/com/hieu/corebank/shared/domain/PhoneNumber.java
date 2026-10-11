package com.hieu.corebank.shared.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object biểu diễn Số điện thoại (đầu số Việt Nam).
 */
public final class PhoneNumber implements Serializable {

    private static final Pattern PATTERN = Pattern.compile("^(?:\\+84|0)(?:3|5|7|8|9)[0-9]{8}$");

    private final String value;

    private PhoneNumber(String value) {
        if (value == null) {
            throw new DomainException("Phone number cannot be null");
        }
        String cleaned = value.replaceAll("\\s+|-", "");
        if (!PATTERN.matcher(cleaned).matches()) {
            throw new DomainException("Invalid phone number format: " + value);
        }
        this.value = cleaned;
    }

    public static PhoneNumber of(String value) {
        return new PhoneNumber(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
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
