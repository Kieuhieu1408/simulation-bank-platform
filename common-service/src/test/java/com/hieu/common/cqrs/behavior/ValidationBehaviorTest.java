package com.hieu.common.cqrs.behavior;

import com.hieu.common.annotation.ValidCommand;
import com.hieu.common.cqrs.Command;
import com.hieu.common.cqrs.RequestHandlerDelegate;
import com.hieu.common.exception.InvalidRequestException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ValidationBehavior Unit Tests")
class ValidationBehaviorTest {

    private ValidationBehavior behavior;

    @ValidCommand
    static class ValidatedCommand implements Command<String> {
        @NotBlank(message = "cifNumber must not be blank")
        private final String cifNumber;

        @Positive(message = "amount must be positive")
        private final BigDecimal amount;

        ValidatedCommand(String cifNumber, BigDecimal amount) {
            this.cifNumber = cifNumber;
            this.amount = amount;
        }
    }

    static class UnannotatedCommand implements Command<String> {
        @NotBlank
        private final String field;

        UnannotatedCommand(String field) {
            this.field = field;
        }
    }

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        behavior = new ValidationBehavior(validator);
    }

    @Test
    @DisplayName("Should pass validation when command fields are valid")
    void testPassesWhenValid() {
        ValidatedCommand cmd = new ValidatedCommand("CIF12345", new BigDecimal("100.00"));
        RequestHandlerDelegate<Object> next = () -> "OK";

        Object result = behavior.handle(cmd, next);
        assertThat(result).isEqualTo("OK");
    }

    @Test
    @DisplayName("Should throw InvalidRequestException when @ValidCommand has violations")
    void testThrowsWhenInvalid() {
        ValidatedCommand cmd = new ValidatedCommand("", new BigDecimal("-10.00"));
        RequestHandlerDelegate<Object> next = () -> "OK";

        assertThatThrownBy(() -> behavior.handle(cmd, next))
                .isInstanceOf(InvalidRequestException.class)
                .satisfies(ex -> {
                    InvalidRequestException ire = (InvalidRequestException) ex;
                    assertThat(ire.details()).hasSize(2);
                });
    }

    @Test
    @DisplayName("Should bypass validation when command lacks @ValidCommand annotation")
    void testBypassesWhenNotAnnotated() {
        UnannotatedCommand cmd = new UnannotatedCommand(""); // invalid field, but not @ValidCommand
        RequestHandlerDelegate<Object> next = () -> "OK";

        Object result = behavior.handle(cmd, next);
        assertThat(result).isEqualTo("OK");
    }
}
