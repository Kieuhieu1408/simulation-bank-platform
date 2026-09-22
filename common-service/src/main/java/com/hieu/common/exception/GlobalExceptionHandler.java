package com.hieu.common.exception;

import java.util.List;

import com.hieu.common.observability.CorrelationIdFilter;
import com.hieu.common.observability.LogFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Điểm duy nhất biến exception thành HTTP response (SRD mục 13.2, 13.3).
 *
 * <p>Nguyên tắc:
 * <ul>
 *   <li>Client chỉ nhận mã trong error catalog và thông báo an toàn.</li>
 *   <li>Stack trace và chi tiết kỹ thuật chỉ nằm ở log nội bộ.</li>
 *   <li>Lỗi không lường trước luôn là {@code 500} với mã chung, không rò
 *       exception class hay message gốc.</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Mọi lỗi đã được phân loại của Money Bank. */
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ApiErrorResponse> handleCommonException(CommonException exception) {
        ErrorCode errorCode = exception.errorCode();
        // Lỗi nghiệp vụ là kết quả hợp lệ của hệ thống nên log ở mức WARN, không
        // kèm stack trace để không tạo nhiễu cho alert.
        log.warn("eventName={} {}={} {}={} reason={}",
                "REQUEST_REJECTED",
                LogFields.ERROR_CODE, errorCode.code(),
                LogFields.ERROR_CATEGORY, errorCode.category(),
                exception.getMessage());
        return respond(errorCode, exception.safeMessage());
    }

    /** Lỗi bean validation ở lớp transport. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        // Chỉ trả tên field và thông báo của constraint. Không phản chiếu giá trị
        // client gửi lên vì giá trị đó có thể là dữ liệu nhạy cảm.
        List<ApiErrorResponse.FieldViolation> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ApiErrorResponse.FieldViolation(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()))
                .toList();
        log.warn("eventName={} {}={} fieldCount={}",
                "REQUEST_VALIDATION_FAILED",
                LogFields.ERROR_CODE, ErrorCode.REQUEST_INVALID.code(),
                details.size());
        return ResponseEntity.status(ErrorCode.REQUEST_INVALID.httpStatus())
                .body(ApiErrorResponse.validation(CorrelationIdFilter.current(), details));
    }

    /**
     * Authorization bị từ chối, gồm cả method authorization.
     *
     * <p>Audit của quyết định deny được ghi tại lớp authorization; ở đây chỉ log
     * theo category, không ghi principal hay token ({@code SEC-004}).
     */
    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<ApiErrorResponse> handleAuthorizationDenied(RuntimeException exception) {
        log.warn("eventName={} {}={}",
                "AUTHORIZATION_DENIED",
                LogFields.ERROR_CODE, ErrorCode.FORBIDDEN.code());
        return respond(ErrorCode.FORBIDDEN, ErrorCode.FORBIDDEN.safeMessage());
    }

    /** Thiếu/sai credential. Không tiết lộ lý do cụ thể để tránh dò token. */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(AuthenticationException exception) {
        log.warn("eventName={} {}={}",
                "AUTHENTICATION_FAILED",
                LogFields.ERROR_CODE, ErrorCode.UNAUTHENTICATED.code());
        return respond(ErrorCode.UNAUTHENTICATED, ErrorCode.UNAUTHENTICATED.safeMessage());
    }

    /**
     * Lưới an toàn cuối. Bất kỳ exception chưa được phân loại đều thành
     * {@code 500} với mã chung; stack trace chỉ ở log nội bộ.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception) {
        log.error("eventName={} {}={}",
                "UNHANDLED_ERROR",
                LogFields.ERROR_CODE, ErrorCode.INTERNAL_ERROR.code(),
                exception);
        return respond(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.safeMessage());
    }

    private ResponseEntity<ApiErrorResponse> respond(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.httpStatus())
                .body(ApiErrorResponse.of(errorCode, message, CorrelationIdFilter.current()));
    }
}
