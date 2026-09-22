package com.hieu.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Global filter: đảm bảo mỗi request có X-Correlation-Id.
 *
 * <p>Logic:
 * <ul>
 *   <li>Nếu client gửi kèm X-Correlation-Id → giữ nguyên (cho phép client tracking)</li>
 *   <li>Nếu không có → sinh UUID v4 mới</li>
 *   <li>Giá trị cuối cùng được: forward xuống backend + trả về client trong response</li>
 * </ul>
 *
 * <p>Order -100: chạy đầu tiên trong filter chain, trước security và rate limit.
 * Đảm bảo correlation ID có mặt trong mọi log từ các filter sau.
 */
@Slf4j
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        final String finalCorrelationId = correlationId;

        // Forward correlation ID xuống backend service
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(CORRELATION_ID_HEADER, finalCorrelationId)
                .build();

        // Thêm correlation ID vào response header trả về client
        exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, finalCorrelationId);

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -100; // Chạy trước tất cả filter khác
    }
}
