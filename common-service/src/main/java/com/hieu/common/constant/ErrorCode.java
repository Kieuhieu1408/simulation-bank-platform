package com.hieu.common.constant;

import org.springframework.http.HttpStatus;

/**
 * Contract mã lỗi của toàn hệ thống.
 *
 * <p>Mỗi service tự định nghĩa enum implement interface này để cung cấp danh mục
 * mã lỗi của riêng mình. {@link CommonErrorCode} cung cấp các mã chung nhất
 * không gắn với domain cụ thể nào.
 *
 * <p>Quy tắc mã số: {@code <HTTP_STATUS><SEQ_3_DIGITS>} (VD: 400001, 404001, 500001).
 * Không dùng tiền tố service (MB-, CB-, ...) vì mã lỗi trong interface này là
 * contract platform-wide.
 */
public interface ErrorCode {

    /** Mã số thuần tuý, không chứa tiền tố. Ví dụ: {@code "400001"}, {@code "500001"}. */
    String code();

    /** HTTP status tương ứng để trả về client. */
    HttpStatus httpStatus();

    /** Phân loại để điều hướng retry/alert logic. */
    ErrorCategory category();

    /**
     * Thông báo an toàn để trả ra ngoài.
     * Không bao giờ chứa stack trace, SQL, hay thông tin nội bộ.
     */
    String safeMessage();

    /** {@code true} nếu client được phép tự động retry. */
    default boolean retryable() {
        return category() == ErrorCategory.TECHNICAL_RETRYABLE;
    }
}
