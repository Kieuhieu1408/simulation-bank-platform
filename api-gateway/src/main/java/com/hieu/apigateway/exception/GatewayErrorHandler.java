package com.hieu.apigateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Global error handler cho API Gateway.
 *
 * <p>Chuyển đổi exception thành JSON error envelope chuẩn.
 * Tránh leak stack trace hoặc internal detail ra client.
 *
 * <p>Error codes:
 * <ul>
 *   <li>{@code AUTH_001} — JWT invalid, expired, wrong issuer</li>
 *   <li>{@code AUTH_002} — Missing Authorization header</li>
 *   <li>{@code GW_429} — Rate limit exceeded</li>
 *   <li>{@code GW_404} — Route not found</li>
 *   <li>{@code GW_502} — Backend service unavailable</li>
 *   <li>{@code GW_500} — Unexpected gateway error</li>
 * </ul>
 */
@Slf4j
@Component
@Order(-2) // Phải cao hơn default WebFlux error handler (order -1)
@RequiredArgsConstructor
public class GatewayErrorHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        String code;
        String message;

        if (ex instanceof InvalidBearerTokenException) {
            status = HttpStatus.UNAUTHORIZED;
            code = "AUTH_001";
            message = "Token invalid or expired";
        } else if (ex instanceof org.springframework.security.core.AuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
            code = "AUTH_002";
            message = "Authentication required";
        } else if (ex instanceof ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            code = "GW_" + status.value();
            message = switch (status.value()) {
                case 404 -> "Route not found";
                case 429 -> "Too many requests. Please slow down";
                case 502 -> "Service temporarily unavailable";
                default -> "Gateway error";
            };
        } else {
            // Unexpected — log đầy đủ nội bộ nhưng trả về message chung
            log.error("Unexpected gateway error: {}", ex.getMessage(), ex);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            code = "GW_500";
            message = "Unexpected error. Please try again";
        }

        String correlationId = exchange.getRequest().getHeaders()
                .getFirst("X-Correlation-Id");

        Map<String, Object> errorBody = Map.of(
                "code", code,
                "message", message,
                "correlationId", correlationId != null ? correlationId : "unknown"
        );

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(errorBody);
        } catch (JsonProcessingException e) {
            bytes = ("{\"code\":\"GW_500\",\"message\":\"Serialization error\"}").getBytes();
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
}
}
