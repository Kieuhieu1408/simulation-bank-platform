package com.hieu.corebank.shared.domain;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object biểu diễn Số thẻ thanh toán (16 chữ số, thẩm định thuật toán Luhn).
 * Tuân thủ Nghị định 13/2023/NĐ-CP và chuẩn PCI DSS (D13).
 */
public final class CardNumber implements Serializable {

    private static final Pattern PATTERN = Pattern.compile("^[0-9]{16}$");
    private static final SecureRandom RANDOM = new SecureRandom();

    private final String value;

    private CardNumber(String value) {
        if (value == null) {
            throw new DomainException("Card number cannot be null");
        }
        String cleaned = value.replaceAll("\\s+|-", "");
        if (!PATTERN.matcher(cleaned).matches()) {
            throw new DomainException("Card number must be exactly 16 digits: " + value);
        }
        if (!isValidLuhn(cleaned)) {
            throw new DomainException("Invalid card number checksum (Luhn check failed): " + value);
        }
        this.value = cleaned;
    }

    public static CardNumber of(String value) {
        return new CardNumber(value);
    }

    /**
     * Sinh số thẻ 16 số hợp lệ theo thuật toán Luhn với BIN đầu vào (vd: 9704, 4111).
     */
    public static CardNumber generate(String bin) {
        if (bin == null || bin.length() > 15) {
            bin = "9704";
        }
        int remainingLength = 15 - bin.length();
        StringBuilder sb = new StringBuilder(bin);
        for (int i = 0; i < remainingLength; i++) {
            sb.append(RANDOM.nextInt(10));
        }

        // Tính check digit Luhn
        int checkDigit = calculateLuhnCheckDigit(sb.toString());
        sb.append(checkDigit);
        return new CardNumber(sb.toString());
    }

    public static boolean isValidLuhn(String number) {
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = number.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    private static int calculateLuhnCheckDigit(String numberWithoutCheck) {
        int sum = 0;
        boolean alternate = true;
        for (int i = numberWithoutCheck.length() - 1; i >= 0; i--) {
            int n = numberWithoutCheck.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        int mod = sum % 10;
        return (mod == 0) ? 0 : 10 - mod;
    }

    public String value() {
        return value;
    }

    /**
     * Che mờ PAN thẻ theo chuẩn D13 & PCI DSS:
     * 4111-22XX-XXXX-3344
     */
    public String mask() {
        return value.substring(0, 4) + "-" + value.substring(4, 6) + "XX-XXXX-" + value.substring(12, 16);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardNumber that = (CardNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return mask();
    }
}
