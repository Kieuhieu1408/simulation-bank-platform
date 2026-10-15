package com.hieu.corebank.shared.infrastructure.web;

import com.hieu.common.exception.ApiErrorResponse;
import com.hieu.common.observability.CorrelationIdFilter;
import com.hieu.corebank.shared.domain.BusinessException;
import com.hieu.corebank.shared.domain.ConflictException;
import com.hieu.corebank.shared.domain.DomainException;
import com.hieu.corebank.shared.domain.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Ánh xạ domain exceptions của corebank sang {@link ApiErrorResponse} chuẩn.
 *
 * <p>Domain exceptions ({@link DomainException} và các subclass) không extend
 * {@code CommonException} để giữ sạch domain layer (ArchUnit Rule 1b). Handler
 * này làm nhiệm vụ mapping tại infrastructure/web layer.
 *
 * <p>Quy tắc logging:
 * <ul>
 *   <li>4xx business rejections → {@code WARN} không kèm stack trace</li>
 *   <li>5xx unexpected → {@code ERROR} kèm full stack trace</li>
 * </ul>
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    // --- Mã lỗi số của corebank (định nghĩa nội bộ, không share ra ngoài) ---
    private static final String CODE_NOT_FOUND         = "404001";
    private static final String CODE_CONFLICT          = "409001";
    private static final String CODE_BUSINESS          = "422001";
    private static final String CODE_BAD_REQUEST       = "400001";
    private static final String CODE_INTERNAL          = "500001";

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> notFound(NotFoundException ex) {
        log.warn("eventName=DOMAIN_NOT_FOUND errorCode={} reason={}", CODE_NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(CODE_NOT_FOUND, ex.getMessage(),
                        CorrelationIdFilter.current(), List.of()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> conflict(ConflictException ex) {
        log.warn("eventName=DOMAIN_CONFLICT errorCode={} reason={}", CODE_CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(CODE_CONFLICT, ex.getMessage(),
                        CorrelationIdFilter.current(), List.of()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> businessRuleViolation(BusinessException ex) {
        log.warn("eventName=DOMAIN_BUSINESS_VIOLATION errorCode={} reason={}", CODE_BUSINESS, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiErrorResponse(CODE_BUSINESS, ex.getMessage(),
                        CorrelationIdFilter.current(), List.of()));
    }

    @ExceptionHandler({DomainException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErrorResponse> badRequest(Exception ex) {
        log.warn("eventName=DOMAIN_BAD_REQUEST errorCode={} reason={}", CODE_BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(CODE_BAD_REQUEST, ex.getMessage(),
                        CorrelationIdFilter.current(), List.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException ex) {
        List<ApiErrorResponse.FieldViolation> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> new ApiErrorResponse.FieldViolation(e.getField(), e.getDefaultMessage()))
                .toList();

        log.warn("eventName=DOMAIN_VALIDATION_FAILED errorCode={} fieldCount={}",
                CODE_BAD_REQUEST, details.size());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(CODE_BAD_REQUEST, "Dữ liệu yêu cầu không hợp lệ",
                        CorrelationIdFilter.current(), details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> unexpected(Exception ex) {
        log.error("eventName=DOMAIN_UNHANDLED_ERROR errorCode={}", CODE_INTERNAL, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(CODE_INTERNAL, "Hệ thống đang gặp sự cố",
                        CorrelationIdFilter.current(), List.of()));
    }
}
