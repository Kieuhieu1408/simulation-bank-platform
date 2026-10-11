package com.hieu.corebank.shared.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object biểu diễn Số Định danh Cá nhân (CCCD 12 số hoặc Hộ chiếu).
 * Tuân thủ Nghị định 13/2023/NĐ-CP về bảo vệ dữ liệu cá nhân.
 */
public final class NationalId implements Serializable {

    private static final Pattern CCCD_PATTERN     = Pattern.compile("^[0-9]{12}$");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z][0-9]{7,8}$");

    private final String value;

    private NationalId(String value) {
        if (value == null) {
            throw new DomainException("National ID cannot be null");
        }
        String trimmed = value.trim();
        if (!CCCD_PATTERN.matcher(trimmed).matches() && !PASSPORT_PATTERN.matcher(trimmed).matches()) {
            throw new DomainException("Invalid National ID format (must be 12-digit CCCD or valid passport): " + value);
        }
        this.value = trimmed;
    }

    public static NationalId of(String value) {
        return new NationalId(value);
    }

    public String value() {
        return value;
    }

    /**
     * Che mờ dữ liệu theo chuẩn D13 & NĐ 13/2023/NĐ-CP:
     * CCCD: 012345****01 (giữ 6 số đầu và 2 số cuối)
     */
    public String mask() {
        if (value.length() == 12) {
            return value.substring(0, 6) + "****" + value.substring(10);
        }
        // Cho hộ chiếu
        return value.substring(0, 2) + "****" + value.substring(value.length() - 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NationalId that = (NationalId) o;
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
