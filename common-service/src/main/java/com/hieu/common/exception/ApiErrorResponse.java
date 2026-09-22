package com.hieu.common.exception;

import java.util.List;

/**
 * Error envelope chuẩn theo SRD mục 13.3.
 *
 * <p>Đây là contract public. Không thêm field chứa thông tin nội bộ như
 * exception class, SQL state, hostname hay raw error của Corebank.
 *
 * @param code          mã trong {@link ErrorCode}
 * @param message       thông báo an toàn cho người dùng cuối
 * @param correlationId để đối soát với log/trace, lấy từ MDC
 * @param details       lỗi theo từng field, chỉ dùng cho lỗi validation
 */
public record ApiErrorResponse(
        String code,
        String message,
        String correlationId,
        List<FieldViolation> details) {

    public ApiErrorResponse {
        details = details == null ? List.of() : List.copyOf(details);
    }

    public static ApiErrorResponse of(ErrorCode errorCode, String correlationId) {
        return new ApiErrorResponse(errorCode.code(), errorCode.safeMessage(), correlationId, List.of());
    }

    public static ApiErrorResponse of(ErrorCode errorCode, String message, String correlationId) {
        return new ApiErrorResponse(errorCode.code(), message, correlationId, List.of());
    }

    public static ApiErrorResponse validation(String correlationId, List<FieldViolation> details) {
        return new ApiErrorResponse(
                ErrorCode.REQUEST_INVALID.code(),
                ErrorCode.REQUEST_INVALID.safeMessage(),
                correlationId,
                details);
    }

    /**
     * Một lỗi validation ở mức field.
     *
     * @param field   tên field trong request
     * @param message lý do an toàn; không chứa giá trị người dùng gửi lên để
     *                tránh phản chiếu dữ liệu nhạy cảm
     */
    public record FieldViolation(String field, String message) {
    }
}
