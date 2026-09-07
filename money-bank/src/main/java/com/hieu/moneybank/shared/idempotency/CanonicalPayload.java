package com.hieu.moneybank.shared.idempotency;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Currency;
import java.util.Map;
import java.util.TreeMap;

/**
 * Dạng chuẩn hóa của payload nghiệp vụ, dùng làm đầu vào cho request hash
 * ({@code AD-DC-H01}..{@code AD-DC-H06}).
 *
 * <p>Vì sao cần canonical hóa thay vì hash trực tiếp JSON body: JSON có nhiều
 * biểu diễn cho cùng một ý định (thứ tự field, khoảng trắng, {@code 100} so với
 * {@code 100.00}, Unicode tổ hợp so với dựng sẵn). Hash thô sẽ coi hai request
 * giống nhau về nghiệp vụ là khác nhau, làm mất tác dụng chống trùng — đúng lỗi
 * {@code DV-F-05}.
 *
 * <p>Chỉ đưa vào đây field có ý nghĩa nghiệp vụ. Correlation id, trace, user
 * agent và timestamp gửi request không được tham gia hash ({@code AD-DC-H05}),
 * nếu không mọi lần retry đều tạo hash mới và conflict giả.
 */
public final class CanonicalPayload {

    /**
     * Version của thuật toán canonical hóa. Đổi quy tắc chuẩn hóa là breaking
     * change nên phải tăng version, không được sửa im lặng ({@code AD-DC-H06}).
     */
    public static final String CANONICAL_VERSION = "v1";

    private static final char FIELD_SEPARATOR = '\n';
    private static final char KEY_VALUE_SEPARATOR = '=';
    private static final String NULL_MARKER = "\u0000null";

    /** TreeMap để thứ tự field luôn xác định, không phụ thuộc thứ tự gọi builder. */
    private final Map<String, String> fields = new TreeMap<>();

    private CanonicalPayload() {
    }

    public static CanonicalPayload builder() {
        return new CanonicalPayload();
    }

    /**
     * Thêm field dạng chuỗi. Giá trị được trim và chuẩn hóa Unicode NFC.
     *
     * <p>{@code null} được ghi bằng marker riêng để phân biệt với chuỗi rỗng:
     * "không gửi field" và "gửi field rỗng" là hai ý định khác nhau.
     */
    public CanonicalPayload text(String key, String value) {
        requireKey(key);
        fields.put(key, value == null ? NULL_MARKER : normalize(value));
        return this;
    }

    /**
     * Thêm số tiền, chuẩn hóa theo số chữ số thập phân của đơn vị tiền tệ.
     *
     * <p>Nhờ đó {@code 100} và {@code 100.00} VND cho cùng hash, còn thay đổi số
     * tiền thật thì hash đổi. Dùng {@link BigDecimal} là bắt buộc theo
     * {@code TRF-INV-003}: {@code double} không biểu diễn chính xác số tiền.
     *
     * @throws IllegalArgumentException nếu currency không hợp lệ hoặc số tiền có
     *         độ chính xác cao hơn mức đơn vị tiền tệ cho phép
     */
    public CanonicalPayload money(String key, BigDecimal amount, String currencyCode) {
        requireKey(key);
        if (amount == null || currencyCode == null) {
            fields.put(key, NULL_MARKER);
            return this;
        }
        Currency currency = Currency.getInstance(normalize(currencyCode).toUpperCase());
        int scale = currency.getDefaultFractionDigits();
        // RoundingMode.UNNECESSARY: nếu client gửi 100.005 VND thì đó là dữ liệu
        // sai, phải báo lỗi chứ không được im lặng làm tròn rồi hash giá trị khác
        // với giá trị sẽ gửi xuống Corebank.
        BigDecimal scaled = amount.setScale(scale, java.math.RoundingMode.UNNECESSARY);
        fields.put(key, currency.getCurrencyCode() + ':' + scaled.toPlainString());
        return this;
    }

    /** Thêm field kiểu enum. */
    public CanonicalPayload enumValue(String key, Enum<?> value) {
        requireKey(key);
        fields.put(key, value == null ? NULL_MARKER : value.name());
        return this;
    }

    /**
     * Chuỗi canonical cuối cùng.
     *
     * <p>Định dạng {@code version\nkey=value\nkey=value}. Key không được chứa ký
     * tự phân cách nên không thể dựng hai payload khác nhau cho cùng một chuỗi.
     */
    public String canonicalForm() {
        StringBuilder builder = new StringBuilder(CANONICAL_VERSION);
        for (Map.Entry<String, String> field : fields.entrySet()) {
            builder.append(FIELD_SEPARATOR)
                    .append(field.getKey())
                    .append(KEY_VALUE_SEPARATOR)
                    .append(field.getValue());
        }
        return builder.toString();
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFC);
    }

    private static void requireKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Canonical field key không được rỗng");
        }
        if (key.indexOf(FIELD_SEPARATOR) >= 0 || key.indexOf(KEY_VALUE_SEPARATOR) >= 0) {
            throw new IllegalArgumentException("Canonical field key không được chứa ký tự phân cách: " + key);
        }
    }
}
