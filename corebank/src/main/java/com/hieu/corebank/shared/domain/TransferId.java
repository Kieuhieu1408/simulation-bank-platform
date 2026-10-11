package com.hieu.corebank.shared.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object biểu diễn Mã giao dịch chuyển tiền (TransferId).
 */
public final class TransferId implements Serializable {

    private final String value;

    private TransferId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("TransferId cannot be null or blank");
        }
        this.value = value.trim();
    }

    public static TransferId of(String value) {
        return new TransferId(value);
    }

    public static TransferId generate() {
        return new TransferId(UUID.randomUUID().toString());
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferId that = (TransferId) o;
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
