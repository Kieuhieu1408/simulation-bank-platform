package com.hieu.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global filter: inject X-User-Id và X-User-Roles vào request sau khi JWT đã validate.
 *
 * <p>Mục đích: Backend service không cần parse JWT — chỉ đọc header để lấy user context.
 * Điều này giảm coupling giữa service và JWT structure.
 *
 * <p><b>Security quan trọng:</b> Filter này phải XÓA header X-User-Id và X-User-Roles
 * từ request gốc của client TRƯỚC khi inject giá trị từ JWT đã validate.
 * Nếu không, client có thể tự inject header giả mạo để leo thang đặc quyền.
 *
 * <p>Order -90: chạy sau CorrelationIdFilter (-100) và sau Spring Security filter
 * (security filter chain chạy trước gateway filter chain).
 *
 * <p>Claims được extract:
 * <ul>
 *   <li>{@code sub} → {@code X-User-Id}</li>
 *   <li>{@code realm_access.roles} → {@code X-User-Roles} (comma-separated)</li>
 * </ul>
 */
@Slf4j
@Component
public class HeaderEnrichmentFilter implements GlobalFilter, Ordered {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_ROLES_HEADER = "X-User-Roles";

    // Headers mà client không được phép inject — gateway strip và tự set
    private static final List<String> PROTECTED_HEADERS = List.of(USER_ID_HEADER, USER_ROLES_HEADER);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .filter(auth -> auth instanceof JwtAuthenticationToken && auth.isAuthenticated())
                .map(auth -> (JwtAuthenticationToken) auth)
                .flatMap(jwtAuth -> {
                    Jwt jwt = jwtAuth.getToken();
                    String userId = jwt.getSubject();
                    String roles = extractRoles(jwt);

                    // Strip client-injected protected headers, inject từ JWT đã validate
                    var mutatedRequest = exchange.getRequest().mutate()
                            .headers(headers -> {
                                PROTECTED_HEADERS.forEach(headers::remove);
                                headers.set(USER_ID_HEADER, userId);
                                if (roles != null && !roles.isBlank()) {
                                    headers.set(USER_ROLES_HEADER, roles);
                                }
                            })
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                .switchIfEmpty(chain.filter(exchange)); // Unauthenticated: pass through (security sẽ block)
    }

    /**
     * Extract roles từ Keycloak claim: realm_access.roles (List<String>)
     * Trả về comma-separated string, ví dụ: "cms-operator,user"
     */
    @SuppressWarnings("unchecked")
    private String extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) {
            return null;
        }
        Object rolesObj = realmAccess.get("roles");
        if (rolesObj instanceof List<?> rolesList) {
            return rolesList.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
        }
        return null;
    }

    @Override
    public int getOrder() {
        return -90; // Sau CorrelationIdFilter, sau Spring Security authentication
    }
}
