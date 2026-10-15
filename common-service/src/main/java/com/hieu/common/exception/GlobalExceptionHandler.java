package com.hieu.common.exception;

import com.hieu.common.constant.CommonErrorCode;
import com.hieu.common.constant.ErrorCategory;
import com.hieu.common.constant.ErrorCode;
import com.hieu.common.observability.CorrelationIdFilter;
import com.hieu.common.observability.LogFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Điểm duy nhất biến exception thành HTTP response (SRD mục 13.2, 13.3).
 *
 * <p>Nguyên tắc:
 * <ul>
 *   <li>Client chỉ nhận mã trong error catalog và thông báo an toàn.</li>
 *   <li>Stack trace và chi tiết kỹ thuật chỉ nằm ở log nội bộ.</li>
 *   <li>Lỗi không lường trước luôn là {@code 500} với mã chung, không rò
 *       exception class hay message gốc.</li>
 *   <li>Đưa {@code errorCode} và {@code errorCategory} vào MDC để hệ thống log tập trung
 *       (ELK/Loki) có thể tạo dashboard và alert chính xác.</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Mọi lỗi đã được phân loại của hệ thống. */
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ApiErrorResponse> handleCommonException(CommonException exception) {
        ErrorCode errorCode = exception.errorCode();
        try {
            enrichMdc(errorCode.code(), errorCode.category().name());

            if (errorCode.category() == ErrorCategory.TECHNICAL_RETRYABLE || exception.getCause() != null) {
                log.error("eventName={} {}={} {}={} reason={}",
                        LogFields.EVENT_REQUEST_REJECTED,
                        LogFields.ERROR_CODE, errorCode.code(),
                        LogFields.ERROR_CATEGORY, errorCode.category(),
                        exception.getMessage(),
                        exception);
            } else {
                log.warn("eventName={} {}={} {}={} reason={}",
                        LogFields.EVENT_REQUEST_REJECTED,
                        LogFields.ERROR_CODE, errorCode.code(),
                        LogFields.ERROR_CATEGORY, errorCode.category(),
                        exception.getMessage());
            }

            if (!exception.fieldViolations().isEmpty()) {
                return ResponseEntity.status(errorCode.httpStatus())
                        .body(new ApiErrorResponse(
                                errorCode.code(),
                                exception.safeMessage(),
                                CorrelationIdFilter.current(),
                                exception.fieldViolations()));
            }

            return respond(errorCode, exception.safeMessage());
        } finally {
            clearMdcErrorFields();
        }
    }

    /** Lỗi bean validation ở lớp transport (@RequestBody). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        try {
            enrichMdc(CommonErrorCode.REQUEST_INVALID.code(), ErrorCategory.VALIDATION.name());

            List<ApiErrorResponse.FieldViolation> details = exception.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(fieldError -> new ApiErrorResponse.FieldViolation(
                            fieldError.getField(),
                            fieldError.getDefaultMessage()))
                    .toList();

            log.warn("eventName={} {}={} fieldCount={}",
                    LogFields.EVENT_REQUEST_VALIDATION_FAILED,
                    LogFields.ERROR_CODE, CommonErrorCode.REQUEST_INVALID.code(),
                    details.size());

            return ResponseEntity.status(CommonErrorCode.REQUEST_INVALID.httpStatus())
                    .body(ApiErrorResponse.validation(CorrelationIdFilter.current(), details));
        } finally {
            clearMdcErrorFields();
        }
    }

    /** Authorization bị từ chối, gồm cả method authorization. */
    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<ApiErrorResponse> handleAuthorizationDenied(RuntimeException exception) {
        try {
            enrichMdc(CommonErrorCode.FORBIDDEN.code(), ErrorCategory.AUTHORIZATION.name());
            log.warn("eventName={} {}={}",
                    LogFields.EVENT_AUTHORIZATION_DENIED,
                    LogFields.ERROR_CODE, CommonErrorCode.FORBIDDEN.code());
            return respond(CommonErrorCode.FORBIDDEN, CommonErrorCode.FORBIDDEN.safeMessage());
        } finally {
            clearMdcErrorFields();
        }
    }

    /** Thiếu/sai credential. Không tiết lộ lý do cụ thể để tránh dò token. */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(AuthenticationException exception) {
        try {
            enrichMdc(CommonErrorCode.UNAUTHENTICATED.code(), ErrorCategory.AUTHENTICATION.name());
            log.warn("eventName={} {}={}",
                    LogFields.EVENT_AUTHENTICATION_FAILED,
                    LogFields.ERROR_CODE, CommonErrorCode.UNAUTHENTICATED.code());
            return respond(CommonErrorCode.UNAUTHENTICATED, CommonErrorCode.UNAUTHENTICATED.safeMessage());
        } finally {
            clearMdcErrorFields();
        }
    }

    /** Lưới an toàn cuối. Bất kỳ exception chưa được phân loại đều thành 500 với mã chung. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception) {
        try {
            enrichMdc(CommonErrorCode.INTERNAL_ERROR.code(), ErrorCategory.TECHNICAL_RETRYABLE.name());
            log.error("eventName={} {}={}",
                    LogFields.EVENT_UNHANDLED_ERROR,
                    LogFields.ERROR_CODE, CommonErrorCode.INTERNAL_ERROR.code(),
                    exception);
            return respond(CommonErrorCode.INTERNAL_ERROR, CommonErrorCode.INTERNAL_ERROR.safeMessage());
        } finally {
            clearMdcErrorFields();
        }
    }

    private void enrichMdc(String errorCode, String errorCategory) {
        MDC.put(LogFields.ERROR_CODE, errorCode);
        MDC.put(LogFields.ERROR_CATEGORY, errorCategory);
    }

    private void clearMdcErrorFields() {
        MDC.remove(LogFields.ERROR_CODE);
        MDC.remove(LogFields.ERROR_CATEGORY);
    }

    private ResponseEntity<ApiErrorResponse> respond(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.httpStatus())
                .body(ApiErrorResponse.of(errorCode, message, CorrelationIdFilter.current()));
    }
}
