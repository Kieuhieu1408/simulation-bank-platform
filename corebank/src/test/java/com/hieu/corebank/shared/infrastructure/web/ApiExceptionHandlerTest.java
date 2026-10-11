package com.hieu.corebank.shared.infrastructure.web;

import com.hieu.corebank.shared.domain.ConflictException;
import com.hieu.corebank.shared.domain.DomainException;
import com.hieu.corebank.shared.domain.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiExceptionHandler Unit Tests")
class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    @DisplayName("DomainException maps to HTTP 400 Bad Request")
    void testDomainExceptionMapsToBadRequest() {
        DomainException ex = new DomainException("Invalid currency format");
        ApiExceptionHandler.ErrorResponse response = handler.badRequest(ex);

        assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.error()).isEqualTo("Bad Request");
        assertThat(response.message()).isEqualTo("Invalid currency format");
    }

    @Test
    @DisplayName("NotFoundException maps to HTTP 404 Not Found")
    void testNotFoundExceptionMapsToNotFound() {
        NotFoundException ex = new NotFoundException("Account not found");
        ApiExceptionHandler.ErrorResponse response = handler.notFound(ex);

        assertThat(response.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.error()).isEqualTo("Not Found");
        assertThat(response.message()).isEqualTo("Account not found");
    }

    @Test
    @DisplayName("ConflictException maps to HTTP 409 Conflict")
    void testConflictExceptionMapsToConflict() {
        ConflictException ex = new ConflictException("Idempotency conflict");
        ApiExceptionHandler.ErrorResponse response = handler.conflict(ex);

        assertThat(response.status()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.error()).isEqualTo("Conflict");
        assertThat(response.message()).isEqualTo("Idempotency conflict");
    }

    @Test
    @DisplayName("BusinessException maps to HTTP 422 Unprocessable Entity")
    void testBusinessExceptionMapsToUnprocessableEntity() {
        com.hieu.corebank.shared.domain.BusinessException ex =
                new com.hieu.corebank.shared.domain.BusinessException("Insufficient available balance");
        ApiExceptionHandler.ErrorResponse response = handler.businessRuleViolation(ex);

        assertThat(response.status()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(response.error()).isEqualTo("Unprocessable Entity");
        assertThat(response.message()).isEqualTo("Insufficient available balance");
    }
}
