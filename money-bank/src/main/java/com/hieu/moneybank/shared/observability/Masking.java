package com.hieu.moneybank.shared.observability;

/**
 * Mask dữ liệu nhạy cảm trước khi đưa vào log, metric, trace hoặc response
 * ({@code SEC-004}, BRD 3.2.1).
 *
 * <p>Quy tắc: chỉ giữ 4 ký tự cuối của số tài khoản. Chuỗi quá ngắn để mask an
 * toàn thì mask toàn bộ, không "cố giữ" một phần.
 */
public final class Masking {

    private static final int VISIBLE_SUFFIX_LENGTH = 4;
    private static final String FULLY_MASKED = "****";

    private Masking() {
    }

    /**
     * Mask số tài khoản, chỉ để lộ 4 số cuối.
     *
     * @return {@code null} nếu đầu vào {@code null}, để phân biệt "không có dữ
     *         liệu" với "dữ liệu đã bị mask"
     */
    public static String accountNumber(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }
        String trimmed = accountNumber.trim();
        if (trimmed.length() <= VISIBLE_SUFFIX_LENGTH) {
            return FULLY_MASKED;
        }
        return "*".repeat(trimmed.length() - VISIBLE_SUFFIX_LENGTH)
                + trimmed.substring(trimmed.length() - VISIBLE_SUFFIX_LENGTH);
    }

    /**
     * Mask định danh dùng để đối soát (customer id, CIF): giữ 4 ký tự cuối.
     * Không dùng cho token, OTP hay secret — những giá trị đó không được log ở
     * bất kỳ dạng nào.
     */
    public static String identifier(String identifier) {
        return accountNumber(identifier);
    }
}
