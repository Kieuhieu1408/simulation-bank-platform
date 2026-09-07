package com.hieu.moneybank.shared.error;

import org.springframework.http.HttpStatus;

/**
 * Error catalog của Money Bank theo SRD mục 13.2 và 13.3.
 *
 * <p>Định dạng mã: {@code MB-<DOMAIN>-<HTTP>-<SEQ>}. Mã là phần của public
 * contract nên không được đổi ý nghĩa sau khi phát hành; thêm trường hợp mới thì
 * thêm mã mới.
 *
 * <p>{@code message} ở đây là thông báo an toàn để trả ra ngoài. Không bao giờ
 * đưa exception nội bộ, raw error của Corebank hay stack trace vào đây
 * ({@code SEC-004}).
 */
public enum ErrorCode {

    // --- Common ---------------------------------------------------------
    REQUEST_INVALID("MB-CMN-400-001", HttpStatus.BAD_REQUEST, ErrorCategory.VALIDATION,
            "Dữ liệu yêu cầu không hợp lệ"),
    RESOURCE_NOT_FOUND("MB-CMN-404-001", HttpStatus.NOT_FOUND, ErrorCategory.BUSINESS_FINAL,
            "Không tìm thấy dữ liệu yêu cầu"),
    RATE_LIMITED("MB-CMN-429-001", HttpStatus.TOO_MANY_REQUESTS, ErrorCategory.TECHNICAL_RETRYABLE,
            "Yêu cầu vượt giới hạn cho phép, vui lòng thử lại sau"),
    INTERNAL_ERROR("MB-CMN-500-001", HttpStatus.INTERNAL_SERVER_ERROR, ErrorCategory.TECHNICAL_RETRYABLE,
            "Hệ thống đang gặp sự cố"),
    DEPENDENCY_UNAVAILABLE("MB-CMN-503-001", HttpStatus.SERVICE_UNAVAILABLE, ErrorCategory.TECHNICAL_RETRYABLE,
            "Dịch vụ phụ thuộc tạm thời không khả dụng"),

    // --- Authentication / Authorization ---------------------------------
    UNAUTHENTICATED("MB-AUT-401-001", HttpStatus.UNAUTHORIZED, ErrorCategory.AUTHENTICATION,
            "Yêu cầu chưa được xác thực"),
    FORBIDDEN("MB-AUT-403-001", HttpStatus.FORBIDDEN, ErrorCategory.AUTHORIZATION,
            "Không có quyền thực hiện yêu cầu này"),

    // --- Idempotency ----------------------------------------------------
    /**
     * Cùng idempotency key nhưng canonical request hash khác. Bắt buộc từ chối
     * theo BRD 3.2.4 và {@code AD-DC-C01}.
     */
    IDEMPOTENCY_CONFLICT("MB-TRF-409-001", HttpStatus.CONFLICT, ErrorCategory.BUSINESS_FINAL,
            "Yêu cầu đã được sử dụng với dữ liệu khác"),

    // --- Transfer -------------------------------------------------------
    TRANSFER_REJECTED("MB-TRF-422-001", HttpStatus.UNPROCESSABLE_ENTITY, ErrorCategory.BUSINESS_FINAL,
            "Giao dịch không thể thực hiện"),
    /**
     * Kết quả tại Corebank chưa xác định. Trả kèm {@code transactionId} để client
     * tra cứu; không được coi là thất bại ({@code TRF-INV-008}).
     */
    TRANSFER_OUTCOME_UNKNOWN("MB-TRF-202-001", HttpStatus.ACCEPTED, ErrorCategory.AMBIGUOUS,
            "Giao dịch đang được xử lý, vui lòng tra cứu trạng thái"),

    // --- Account proposal -----------------------------------------------
    PROPOSAL_STATE_CONFLICT("MB-ACC-409-001", HttpStatus.CONFLICT, ErrorCategory.BUSINESS_FINAL,
            "Yêu cầu đã được xử lý hoặc đã thay đổi trạng thái"),
    PROPOSAL_REJECTED("MB-ACC-422-001", HttpStatus.UNPROCESSABLE_ENTITY, ErrorCategory.BUSINESS_FINAL,
            "Yêu cầu không đáp ứng điều kiện nghiệp vụ");

    private final String code;
    private final HttpStatus httpStatus;
    private final ErrorCategory category;
    private final String safeMessage;

    ErrorCode(String code, HttpStatus httpStatus, ErrorCategory category, String safeMessage) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.category = category;
        this.safeMessage = safeMessage;
    }

    public String code() {
        return code;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public ErrorCategory category() {
        return category;
    }

    public String safeMessage() {
        return safeMessage;
    }

    /** Chỉ {@link ErrorCategory#TECHNICAL_RETRYABLE} được retry tự động. */
    public boolean retryable() {
        return category == ErrorCategory.TECHNICAL_RETRYABLE;
    }
}
