package com.hieu.common.exception;

import com.hieu.common.constant.CommonErrorCode;

import java.util.List;

/**
 * Request sai định dạng ở mức không thể xử lý tiếp, phát hiện ngoài phạm vi bean
 * validation (ví dụ idempotency key không đúng khuôn dạng cho phép) hoặc bởi
 * CQRS ValidationBehavior.
 */
public class InvalidRequestException extends CommonException {

    private final List<ApiErrorResponse.FieldViolation> details;

    public InvalidRequestException(String internalReason) {
        this(internalReason, List.of());
    }

    public InvalidRequestException(String internalReason, List<ApiErrorResponse.FieldViolation> details) {
        super(CommonErrorCode.REQUEST_INVALID, internalReason);
        this.details = details == null ? List.of() : List.copyOf(details);
    }

    public List<ApiErrorResponse.FieldViolation> details() {
        return details;
    }
}
