package com.hieu.common.cqrs.behavior;

import com.hieu.common.constant.ErrorCode;
import com.hieu.common.cqrs.PipelineBehavior;
import com.hieu.common.cqrs.RequestHandlerDelegate;
import com.hieu.common.exception.CommonException;
import com.hieu.common.observability.CorrelationIdFilter;
import com.hieu.common.observability.LogFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Pipeline Behavior chịu trách nhiệm ghi log có cấu trúc và đo lường thời gian
 * thực thi của mọi Command / Query trong hệ thống (SRD mục 2.1, 10.4).
 *
 * <p>Tự động inject thông tin vào log:
 * <ul>
 *   <li>{@code eventName=CQRS_DISPATCH_START / CQRS_DISPATCH_SUCCESS / CQRS_DISPATCH_FAILED}</li>
 *   <li>{@code durationMs}: Thời gian thực thi chính xác của handler</li>
 *   <li>{@code errorCode}, {@code errorCategory}: Nếu thao tác phát sinh {@link CommonException}</li>
 * </ul>
 */
@Component
@Order(100)
public class LoggingAndMetricsBehavior implements PipelineBehavior<Object, Object> {

    private static final Logger log = LoggerFactory.getLogger(LoggingAndMetricsBehavior.class);

    @Override
    public Object handle(Object request, RequestHandlerDelegate<Object> next) {
        String requestName = request.getClass().getSimpleName();
        String correlationId = CorrelationIdFilter.current();
        if (correlationId == null) {
            correlationId = MDC.get(LogFields.CORRELATION_ID);
        }

        long startTime = System.currentTimeMillis();
        log.debug("eventName=CQRS_DISPATCH_START request={} correlationId={}", requestName, correlationId);

        try {
            Object result = next.handle();
            long durationMs = System.currentTimeMillis() - startTime;
            log.info("eventName=CQRS_DISPATCH_SUCCESS request={} durationMs={}", requestName, durationMs);
            return result;
        } catch (CommonException ex) {
            long durationMs = System.currentTimeMillis() - startTime;
            ErrorCode errorCode = ex.errorCode();
            log.warn("eventName=CQRS_DISPATCH_FAILED request={} errorCode={} errorCategory={} durationMs={} reason={}",
                    requestName,
                    errorCode.code(),
                    errorCode.category(),
                    durationMs,
                    ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            long durationMs = System.currentTimeMillis() - startTime;
            log.error("eventName=CQRS_DISPATCH_ERROR request={} durationMs={} errorClass={} message={}",
                    requestName,
                    durationMs,
                    ex.getClass().getSimpleName(),
                    ex.getMessage(),
                    ex);
            throw ex;
        }
    }
}
