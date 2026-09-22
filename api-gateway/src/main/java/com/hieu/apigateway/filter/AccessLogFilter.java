package com.hieu.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Global filter: ghi structured access log cho mỗi request/response.
 *
 * <p>Log format (JSON structured qua logback):
 * <pre>
 * {
 *   "correlationId": "...",
 *   "userId": "****1234",   ← masked (chỉ 4 ký tự cuối)
 *   "method": "POST",
 *   "path": "/api/v1/money/transfers",
 *   "statusCode": 200,
 *   "durationMs": 45
 * }
 * </pre>
 *
 * <p><b>Privacy:</b> userId được mask 4 ký tự cuối. JWT value không được log.
 *
 * <p>Order 0: chạy sau tất cả filter infra (correlation, security, enrichment).
 * Post-filter (after chain.filter) captures response status.
 */
@Slf4j
@Component
public class AccessLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = Instant.now().toEpochMilli();
        ServerHttpRequest request = exchange.getRequest();

        String correlationId = request.getHeaders().getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
        String userId = request.getHeaders().getFirst(HeaderEnrichmentFilter.USER_ID_HEADER);

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    ServerHttpResponse response = exchange.getResponse();
                    long durationMs = Instant.now().toEpochMilli() - startTime;
                    int statusCode = response.getStatusCode() != null
                            ? response.getStatusCode().value()
                            : 0;

                    log.info("ACCESS correlationId={} userId={} method={} path={} status={} durationMs={}",
                            correlationId,
                            maskUserId(userId),
                            request.getMethod(),
                            request.getPath().value(),
                            statusCode,
                            durationMs);
                });
    }

    /**
     * Mask userId: giữ 4 ký tự cuối, che phần còn lại bằng '*'.
     * Ví dụ: "abc-12345678" → "****5678"
     */
    private String maskUserId(String userId) {
        if (userId == null || userId.length() <= 4) {
            return "****";
        }
        return "****" + userId.substring(userId.length() - 4);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
