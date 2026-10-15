package com.hieu.common.exception;

import com.hieu.common.constant.CommonErrorCode;
import com.hieu.common.observability.CorrelationIdFilter;
import com.hieu.common.observability.LogFields;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        MDC.clear();
        MDC.put(LogFields.CORRELATION_ID, "test-correlation-12345");
    }

    @Test
    @DisplayName("Should handle CommonException and return standard ApiErrorResponse")
    void testHandleCommonException() {
        // Dùng CommonErrorCode.BUSINESS_RULE_VIOLATION thay cho ErrorCode.TRANSFER_REJECTED
        CommonException ex = new CommonException(
                CommonErrorCode.BUSINESS_RULE_VIOLATION, "Tài khoản nguồn không đủ số dư");

        ResponseEntity<ApiErrorResponse> response = handler.handleCommonException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(CommonErrorCode.BUSINESS_RULE_VIOLATION.code());
        assertThat(response.getBody().message()).isEqualTo(CommonErrorCode.BUSINESS_RULE_VIOLATION.safeMessage());
        assertThat(response.getBody().correlationId()).isEqualTo("test-correlation-12345");

        // MDC error fields must be cleaned up
        assertThat(MDC.get(LogFields.ERROR_CODE)).isNull();
    }

    @Test
    @DisplayName("Should handle InvalidRequestException with field violations")
    void testHandleInvalidRequestWithViolations() {
        List<ApiErrorResponse.FieldViolation> violations = List.of(
                new ApiErrorResponse.FieldViolation("amount", "must be positive"),
                new ApiErrorResponse.FieldViolation("cifNumber", "must not be blank")
        );
        InvalidRequestException ex = new InvalidRequestException("Validation failed", violations);

        ResponseEntity<ApiErrorResponse> response = handler.handleCommonException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(CommonErrorCode.REQUEST_INVALID.code());
        assertThat(response.getBody().details()).hasSize(2);
        assertThat(response.getBody().details().get(0).field()).isEqualTo("amount");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException and map field errors")
    void testHandleValidationException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("dto", "currency", "must be 3 characters")
        ));

        ResponseEntity<ApiErrorResponse> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(CommonErrorCode.REQUEST_INVALID.code());
        assertThat(response.getBody().details()).hasSize(1);
        assertThat(response.getBody().details().get(0).field()).isEqualTo("currency");
    }

    @Test
    @DisplayName("Should handle AccessDeniedException and return 403 Forbidden")
    void testHandleAuthorizationDenied() {
        AccessDeniedException ex = new AccessDeniedException("Forbidden");

        ResponseEntity<ApiErrorResponse> response = handler.handleAuthorizationDenied(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(CommonErrorCode.FORBIDDEN.code());
    }

    @Test
    @DisplayName("Should handle AuthenticationException and return 401 Unauthorized")
    void testHandleAuthenticationException() {
        AuthenticationException ex = new AuthenticationException("Bad credentials") {};

        ResponseEntity<ApiErrorResponse> response = handler.handleAuthentication(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(CommonErrorCode.UNAUTHENTICATED.code());
    }

    @Test
    @DisplayName("Should handle unexpected Exception and return 500 Internal Server Error without leaking internal message")
    void testHandleUnexpectedException() {
        Exception ex = new RuntimeException("Sensitive database credentials or SQL query failure");

        ResponseEntity<ApiErrorResponse> response = handler.handleUnexpected(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(CommonErrorCode.INTERNAL_ERROR.code());
        assertThat(response.getBody().message()).doesNotContain("Sensitive database");
        assertThat(response.getBody().message()).isEqualTo(CommonErrorCode.INTERNAL_ERROR.safeMessage());
    }
}
