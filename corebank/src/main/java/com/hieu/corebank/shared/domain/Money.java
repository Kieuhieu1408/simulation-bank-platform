package com.hieu.corebank.shared.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object biểu diễn Số tiền và Tiền tệ (Money Pattern).
 *
 * <p>Quy tắc bất biến:
 * <ul>
 *   <li>Immutable, không bao giờ thay đổi giá trị sau khi khởi tạo.
 *   <li>Tự động chuẩn hóa scale theo chuẩn ISO 4217 của Currency (VND scale = 0, USD scale = 2).
 *   <li>Ném lỗi nếu số tiền có phần thập phân vượt quá giới hạn của Currency.
 *   <li>Các phép toán cộng/trừ/so sánh bắt buộc phải cùng loại Currency.
 * </ul>
 */
public final class Money implements Serializable, Comparable<Money> {

    private final BigDecimal amount;
    private final Currency   currency;

    private Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new DomainException("Money amount cannot be null");
        }
        if (currency == null) {
            throw new DomainException("Money currency cannot be null");
        }

        // Kiểm tra scale hợp lệ theo Currency
        BigDecimal stripped = amount.stripTrailingZeros();
        int effectiveScale = Math.max(stripped.scale(), 0);
        if (effectiveScale > currency.fractionDigits()) {
            throw new DomainException(String.format(
                    "Amount %s has fraction digits (%d) exceeding maximum (%d) for currency %s",
                    amount, effectiveScale, currency.fractionDigits(), currency.code()));
        }

        this.currency = currency;
        this.amount = amount.setScale(currency.fractionDigits(), RoundingMode.UNNECESSARY);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, Currency.of(currencyCode));
    }

    public static Money of(long amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public static Money of(long amount, String currencyCode) {
        return new Money(BigDecimal.valueOf(amount), Currency.of(currencyCode));
    }

    public static Money of(double amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public static Money of(double amount, String currencyCode) {
        return new Money(BigDecimal.valueOf(amount), Currency.of(currencyCode));
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public static Money zero(String currencyCode) {
        return zero(Currency.of(currencyCode));
    }

    public BigDecimal amount() {
        return amount;
    }

    public Currency currency() {
        return currency;
    }

    public Money requirePositive() {
        if (!isPositive()) {
            throw new DomainException("Amount must be positive: " + amount + " " + currency.code());
        }
        return this;
    }

    public Money requireNonNegative() {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Amount cannot be negative: " + amount + " " + currency.code());
        }
        return this;
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isGreaterThanOrEqual(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isGreaterThan(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isLessThanOrEqual(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) <= 0;
    }

    private void assertSameCurrency(Money other) {
        if (other == null) {
            throw new DomainException("Cannot operate on null Money");
        }
        if (!this.currency.equals(other.currency)) {
            throw new DomainException(String.format(
                    "Currency mismatch: %s vs %s", this.currency.code(), other.currency.code()));
        }
    }

    @Override
    public int compareTo(Money o) {
        assertSameCurrency(o);
        return this.amount.compareTo(o.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency.code();
    }
}
