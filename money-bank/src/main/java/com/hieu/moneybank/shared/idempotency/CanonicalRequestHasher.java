package com.hieu.moneybank.shared.idempotency;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

/**
 * Sinh request hash từ {@link CanonicalPayload} ({@code AD-DC-H04}).
 *
 * <p>Hash cho phép so sánh "cùng key có cùng dữ liệu hay không" mà không phải
 * lưu lại payload gốc — quan trọng vì payload chứa số tài khoản và nội dung
 * chuyển tiền, những dữ liệu không nên nằm ở dạng rõ trong bảng kỹ thuật
 * ({@code AD-DC-D03}).
 *
 * <p>SHA-256 dùng ở đây để phát hiện dữ liệu khác nhau, không phải để chống đoán
 * giá trị, nên không cần salt hay KDF.
 */
@Component
public class CanonicalRequestHasher {

    private static final String ALGORITHM = "SHA-256";

    /** Độ dài hash hex, dùng để giới hạn cột và validate dữ liệu đọc lên. */
    public static final int HASH_LENGTH = 64;

    public String hash(CanonicalPayload payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Canonical payload không được null");
        }
        return hash(payload.canonicalForm());
    }

    public String hash(String canonicalForm) {
        try {
            // MessageDigest không thread-safe nên tạo instance mới mỗi lần gọi.
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hashed = digest.digest(canonicalForm.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException exception) {
            // SHA-256 là thuật toán bắt buộc của mọi JRE; tới đây là môi trường lỗi.
            throw new IllegalStateException("JRE không hỗ trợ " + ALGORITHM, exception);
        }
    }
}
