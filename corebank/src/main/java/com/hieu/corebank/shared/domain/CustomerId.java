package com.hieu.corebank.shared.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value Object biểu diễn Mã Khách hàng / CIF (CustomerId).
 */
public final class CustomerId implements Serializable {

    private final String value;

    private CustomerId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("CustomerId cannot be null or blank");
        }
        this.value = value.trim();
    }

    public static CustomerId of(String value) {
        return new CustomerId(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerId that = (CustomerId) o;
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
