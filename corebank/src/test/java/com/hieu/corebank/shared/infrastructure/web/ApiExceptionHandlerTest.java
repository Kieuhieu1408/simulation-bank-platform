package com.hieu.corebank.shared.infrastructure.web;

import com.hieu.common.exception.ApiErrorResponse;
import com.hieu.corebank.shared.domain.BusinessException;
import com.hieu.corebank.shared.domain.ConflictException;
import com.hieu.corebank.shared.domain.DomainException;
import com.hieu.corebank.shared.domain.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ApiExceptionHandler Unit Tests")
class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    @DisplayName("DomainException maps to HTTP 400 with numeric error code")
    void testDomainExceptionMapsToBadRequest() {
        DomainException ex = new DomainException("Invalid currency format");
        ResponseEntity<ApiErrorResponse> response = handler.badRequest(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("400001");
        assertThat(response.getBody().message()).isEqualTo("Invalid currency format");
    }

    @Test
    @DisplayName("NotFoundException maps to HTTP 404 with numeric error code")
    void testNotFoundExceptionMapsToNotFound() {
        NotFoundException ex = new NotFoundException("Account not found");
        ResponseEntity<ApiErrorResponse> response = handler.notFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("404001");
        assertThat(response.getBody().message()).isEqualTo("Account not found");
    }

    @Test
    @DisplayName("ConflictException maps to HTTP 409 with numeric error code")
    void testConflictExceptionMapsToConflict() {
        ConflictException ex = new ConflictException("Idempotency conflict");
        ResponseEntity<ApiErrorResponse> response = handler.conflict(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("409001");
        assertThat(response.getBody().message()).isEqualTo("Idempotency conflict");
    }

    @Test
    @DisplayName("BusinessException maps to HTTP 422 with numeric error code")
    void testBusinessExceptionMapsToUnprocessableEntity() {
        BusinessException ex = new BusinessException("Insufficient available balance");
        ResponseEntity<ApiErrorResponse> response = handler.businessRuleViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("422001");
        assertThat(response.getBody().message()).isEqualTo("Insufficient available balance");
    }

    @Test
    @DisplayName("MethodArgumentNotValidException maps to 400 with field violations")
    void testValidationExceptionMapsToFieldViolations() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("dto", "amount", "must be positive")
        ));

        ResponseEntity<ApiErrorResponse> response = handler.validation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("400001");
        assertThat(response.getBody().details()).hasSize(1);
        assertThat(response.getBody().details().get(0).field()).isEqualTo("amount");
    }

    @Test
    @DisplayName("Unexpected Exception maps to HTTP 500 without leaking internal message")
    void testUnexpectedExceptionMapsToInternalError() {
        Exception ex = new RuntimeException("Sensitive SQL error details");
        ResponseEntity<ApiErrorResponse> response = handler.unexpected(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("500001");
        assertThat(response.getBody().message()).doesNotContain("Sensitive SQL");
    }

    @Test
    @DisplayName("Response always includes empty details list when no violations")
    void testResponseAlwaysHasDetailsField() {
        NotFoundException ex = new NotFoundException("Not found");
        ResponseEntity<ApiErrorResponse> response = handler.notFound(ex);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().details()).isNotNull().isEmpty();
    }
}
