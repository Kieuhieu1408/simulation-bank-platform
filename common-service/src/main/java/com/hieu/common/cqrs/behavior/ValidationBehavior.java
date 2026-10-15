package com.hieu.common.cqrs.behavior;

import com.hieu.common.annotation.ValidCommand;
import com.hieu.common.constant.CommonErrorCode;
import com.hieu.common.cqrs.PipelineBehavior;
import com.hieu.common.cqrs.RequestHandlerDelegate;
import com.hieu.common.exception.ApiErrorResponse;
import com.hieu.common.exception.CommonException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Pipeline Behavior thực thi xác thực dữ liệu tự động cho các Command
 * được đánh dấu bởi {@link ValidCommand} (SRD mục 2.1).
 */
@Component
@Order(200)
public class ValidationBehavior implements PipelineBehavior<Object, Object> {

    private static final Logger log = LoggerFactory.getLogger(ValidationBehavior.class);

    private final Validator validator;

    @Autowired
    public ValidationBehavior(@Autowired(required = false) Validator validator) {
        this.validator = validator;
    }

    @Override
    public Object handle(Object request, RequestHandlerDelegate<Object> next) {
        if (validator != null && request.getClass().isAnnotationPresent(ValidCommand.class)) {
            Set<ConstraintViolation<Object>> violations = validator.validate(request);
            if (!violations.isEmpty()) {
                List<ApiErrorResponse.FieldViolation> details = violations.stream()
                        .map(v -> new ApiErrorResponse.FieldViolation(v.getPropertyPath().toString(), v.getMessage()))
                        .toList();

                log.warn("eventName=CQRS_VALIDATION_FAILED request={} violationCount={}",
                        request.getClass().getSimpleName(), details.size());

                throw new CommonException(
                        CommonErrorCode.REQUEST_INVALID,
                        "Dữ liệu command không hợp lệ cho " + request.getClass().getSimpleName(),
                        details);
            }
        }
        return next.handle();
    }
}
