package com.hieu.corebank.shared.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object biểu diễn Mã Tiền tệ chuẩn ISO 4217 (3 ký tự in hoa).
 */
public final class Currency implements Serializable, Comparable<Currency> {

    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z]{3}$");

    public static final Currency VND = Currency.of("VND");
    public static final Currency USD = Currency.of("USD");
    public static final Currency EUR = Currency.of("EUR");

    private final String code;
    private final int fractionDigits;

    private Currency(String code) {
        if (code == null || !CODE_PATTERN.matcher(code).matches()) {
            throw new DomainException("Invalid ISO 4217 currency code: " + code);
        }
        this.code = code;
        this.fractionDigits = resolveFractionDigits(code);
    }

    public static Currency of(String code) {
        if (code == null) {
            throw new DomainException("Currency code cannot be null");
        }
        return new Currency(code.trim().toUpperCase());
    }

    private static int resolveFractionDigits(String code) {
        try {
            java.util.Currency jc = java.util.Currency.getInstance(code);
            return Math.max(jc.getDefaultFractionDigits(), 0);
        } catch (IllegalArgumentException e) {
            if ("VND".equalsIgnoreCase(code)) {
                return 0;
            }
            return 2;
        }
    }

    public String code() {
        return code;
    }

    public int fractionDigits() {
        return fractionDigits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(code, currency.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public int compareTo(Currency o) {
        return this.code.compareTo(o.code);
    }
}
