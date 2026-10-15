package com.hieu.common.exception;

import com.hieu.common.constant.ErrorCode;

import java.util.List;

/**
 * Exception duy nhất của platform. Luôn mang một {@link ErrorCode} đã được ánh xạ
 * sang HTTP status và category — không có đường nào để lỗi lọt ra ngoài mà không
 * có mã phân loại.
 *
 * <p>{@code fieldViolations} chỉ được set khi lỗi validation cần trả chi tiết
 * từng field (VD: từ CQRS {@code ValidationBehavior}). Các trường hợp khác để
 * danh sách rỗng.
 */
public class CommonException extends RuntimeException {

    private final ErrorCode errorCode;
    private final List<ApiErrorResponse.FieldViolation> fieldViolations;

    public CommonException(ErrorCode errorCode, String internalMessage) {
        super(internalMessage);
        this.errorCode = errorCode;
        this.fieldViolations = List.of();
    }

    public CommonException(ErrorCode errorCode, String internalMessage, Throwable cause) {
        super(internalMessage, cause);
        this.errorCode = errorCode;
        this.fieldViolations = List.of();
    }

    public CommonException(ErrorCode errorCode, String internalMessage,
                           List<ApiErrorResponse.FieldViolation> fieldViolations) {
        super(internalMessage);
        this.errorCode = errorCode;
        this.fieldViolations = fieldViolations == null ? List.of() : List.copyOf(fieldViolations);
    }

    public ErrorCode errorCode() {
        return errorCode;
    }

    /**
     * Field-level violations. Rỗng nếu không phải lỗi validation.
     */
    public List<ApiErrorResponse.FieldViolation> fieldViolations() {
        return fieldViolations;
    }

    /**
     * Thông báo an toàn để trả client. Tách khỏi {@link #getMessage()} vì
     * {@code getMessage()} phục vụ log nội bộ và có thể chứa chi tiết kỹ thuật.
     */
    public String safeMessage() {
        return errorCode.safeMessage();
    }
}
