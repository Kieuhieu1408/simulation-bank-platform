package com.hieu.common.constant;

import org.springframework.http.HttpStatus;

/**
 * Danh mục mã lỗi số chung nhất toàn hệ thống (Common Error Codes).
 *
 * <p>Quy tắc:
 * <ul>
 *   <li>Chỉ chứa mã số chuẩn (không chứa tiền tố service như MB, CB, v.v.).</li>
 *   <li>Cấu trúc mã: {@code <HTTP_STATUS><SEQ_3_DIGITS>} (VD: 400001, 404001, 500001).</li>
 * </ul>
 */
public enum CommonErrorCode implements ErrorCode {

    // --- Client / Validation Errors (400) ---
    REQUEST_INVALID("400001", HttpStatus.BAD_REQUEST, ErrorCategory.VALIDATION,
            "Dữ liệu yêu cầu không hợp lệ"),

    // --- Authentication / Authorization Errors (401, 403) ---
    UNAUTHENTICATED("401001", HttpStatus.UNAUTHORIZED, ErrorCategory.AUTHENTICATION,
            "Yêu cầu chưa được xác thực"),
    FORBIDDEN("403001", HttpStatus.FORBIDDEN, ErrorCategory.AUTHORIZATION,
            "Không có quyền thực hiện yêu cầu này"),

    // --- Resource Not Found (404) ---
    RESOURCE_NOT_FOUND("404001", HttpStatus.NOT_FOUND, ErrorCategory.BUSINESS_FINAL,
            "Không tìm thấy dữ liệu yêu cầu"),

    // --- Idempotency Conflict (409) ---
    IDEMPOTENCY_CONFLICT("409001", HttpStatus.CONFLICT, ErrorCategory.BUSINESS_FINAL,
            "Yêu cầu đã được sử dụng với dữ liệu khác"),

    // --- Business Rule Violation (422) ---
    BUSINESS_RULE_VIOLATION("422001", HttpStatus.UNPROCESSABLE_ENTITY, ErrorCategory.BUSINESS_FINAL,
            "Yêu cầu vi phạm quy tắc nghiệp vụ"),

    // --- Ambiguous / Async Processing (202) ---
    OPERATION_OUTCOME_UNKNOWN("202001", HttpStatus.ACCEPTED, ErrorCategory.AMBIGUOUS,
            "Giao dịch đang được xử lý, vui lòng tra cứu trạng thái"),

    // --- Rate Limiting (429) ---
    RATE_LIMITED("429001", HttpStatus.TOO_MANY_REQUESTS, ErrorCategory.TECHNICAL_RETRYABLE,
            "Yêu cầu vượt giới hạn cho phép, vui lòng thử lại sau"),

    // --- Server Errors (500, 503) ---
    INTERNAL_ERROR("500001", HttpStatus.INTERNAL_SERVER_ERROR, ErrorCategory.TECHNICAL_RETRYABLE,
            "Hệ thống đang gặp sự cố"),
    DEPENDENCY_UNAVAILABLE("503001", HttpStatus.SERVICE_UNAVAILABLE, ErrorCategory.TECHNICAL_RETRYABLE,
            "Dịch vụ phụ thuộc tạm thời không khả dụng");

    private final String code;
    private final HttpStatus httpStatus;
    private final ErrorCategory category;
    private final String safeMessage;

    CommonErrorCode(String code, HttpStatus httpStatus, ErrorCategory category, String safeMessage) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.category = category;
        this.safeMessage = safeMessage;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public ErrorCategory category() {
        return category;
    }

    @Override
    public String safeMessage() {
        return safeMessage;
    }
}
