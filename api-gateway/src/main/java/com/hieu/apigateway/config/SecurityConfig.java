package com.hieu.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Security configuration cho API Gateway.
 *
 * <p>Gateway là reactive (WebFlux) nên dùng {@link ServerHttpSecurity}
 * thay vì {@link org.springframework.security.config.annotation.web.builders.HttpSecurity}.
 *
 * <p>Chiến lược:
 * <ul>
 *   <li>/actuator/health — public (cho load balancer health check)</li>
 *   <li>/actuator/prometheus — public tại gateway, restrict bằng network policy ở production</li>
 *   <li>Tất cả route khác → yêu cầu JWT hợp lệ từ Keycloak</li>
 * </ul>
 *
 * <p>JWT được validate trực tiếp qua JWKS public key của Keycloak.
 * Gateway KHÔNG introspect token — giảm latency và runtime dependency vào Keycloak
 * cho mỗi request. Trade-off: token revocation không tức thì.
 *
 * <p><b>CORS:</b> Xử lý tập trung tại gateway. Backend service KHÔNG cần config CORS riêng.
 * Browser clients được phép:
 * <ul>
 *   <li>CMS Frontend (Angular): {@code localhost:4200} / {@code CMS_FRONTEND_ORIGIN}</li>
 *   <li>Profile Frontend (ReactJS): {@code localhost:3000} / {@code PROFILE_FRONTEND_ORIGIN}</li>
 *   <li>Mobile App: không cần CORS (không chạy trong browser)</li>
 * </ul>
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${CMS_FRONTEND_ORIGIN:}")
    private String cmsFrontendOrigin;

    @Value("${PROFILE_FRONTEND_ORIGIN:}")
    private String profileFrontendOrigin;

    /**
     * Security filter chain cho WebFlux gateway.
     *
     * <p>CORS phải được đăng ký ở đây (không chỉ trong application.yaml) vì
     * Spring Security filter chạy TRƯỚC gateway CORS handler. Nếu chỉ config
     * trong yaml, preflight OPTIONS request sẽ bị Spring Security block với 401
     * trước khi đến được CORS handler.
     *
     * <p>Header enrichment (X-User-Id, X-User-Roles) được xử lý tại
     * {@link com.hieu.apigateway.filter.HeaderEnrichmentFilter}.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                // CORS phải được enable ở security layer để Spring Security
                // gọi corsConfigurationSource() thay vì block OPTIONS preflight
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchanges -> exchanges
                        // Preflight OPTIONS request phải permit — không cần JWT
                        // (Spring Security 6+ với cors() đã tự xử lý, nhưng explicit rõ ràng hơn)
                        .pathMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // Health probe — public cho load balancer
                        .pathMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        // Prometheus — restrict qua network policy ở production
                        .pathMatchers("/actuator/prometheus").permitAll()
                        // Tất cả API route đều cần authenticate
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                            // jwk-set-uri được cấu hình trong application.yaml
                            // Spring Security tự fetch và cache JWKS keys từ Keycloak
                        })
                )
                .build();
    }

    /**
     * CORS configuration source — định nghĩa whitelist origin, methods, headers.
     *
     * <p>Nguyên tắc:
     * <ul>
     *   <li>Không dùng wildcard {@code *} khi {@code allowCredentials=true} — browser block</li>
     *   <li>Chỉ whitelist origin cụ thể — không accept {@code null} hoặc empty string</li>
     *   <li>Mobile App không có origin (không phải browser) — không cần whitelist</li>
     * </ul>
     *
     * <p>Production: set {@code CMS_FRONTEND_ORIGIN} và {@code PROFILE_FRONTEND_ORIGIN}
     * thành HTTPS domain thực sự (vd: {@code https://cms.simulation-bank.internal}).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Build allowed origins — chỉ thêm origin không rỗng
        List<String> allowedOrigins = new ArrayList<>();
        allowedOrigins.add("http://localhost:4200");   // CMS Angular — local dev
        allowedOrigins.add("http://localhost:3000");   // Profile ReactJS — local dev

        // Production origins từ environment variable (có thể rỗng khi local)
        if (cmsFrontendOrigin != null && !cmsFrontendOrigin.isBlank()) {
            allowedOrigins.add(cmsFrontendOrigin);
        }
        if (profileFrontendOrigin != null && !profileFrontendOrigin.isBlank()) {
            allowedOrigins.add(profileFrontendOrigin);
        }

        config.setAllowedOrigins(allowedOrigins);

        // Methods: bao gồm OPTIONS cho preflight
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Headers client được phép gửi
        config.setAllowedHeaders(List.of(
                "Authorization",       // JWT Bearer token
                "Content-Type",        // application/json
                "X-Correlation-Id",    // Client-side correlation tracking
                "X-Requested-With",    // AJAX detection
                "Accept",
                "Origin"
        ));

        // Headers client được phép đọc từ response
        config.setExposedHeaders(List.of(
                "X-Correlation-Id",      // Để client log/trace
                "X-RateLimit-Remaining", // Để client biết quota còn lại
                "X-RateLimit-Limit",
                "X-RateLimit-Reset"
        ));

        // Cho phép browser gửi credentials (Authorization header)
        // Bắt buộc true nếu dùng Bearer token trong Authorization header
        config.setAllowCredentials(true);

        // Browser cache preflight result 1 giờ — giảm OPTIONS round-trip
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
