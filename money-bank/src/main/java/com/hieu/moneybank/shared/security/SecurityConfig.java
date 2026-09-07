package com.hieu.moneybank.shared.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import java.time.Duration;

/**
 * Cấu hình resource server của Money Bank.
 *
 * <p>Thiết kế theo {@code AD-SEC-P02} và {@code AD-SEC-P03}: Gateway đã kiểm tra
 * thô nhưng service này vẫn tự xác minh issuer, audience và thời hạn. Mạng
 * private không được coi là authentication ({@code ADR-12}).
 *
 * <p>Quy tắc quan trọng: {@code anyRequest().denyAll()}. Mỗi endpoint mới phải
 * được khai báo quyền tường minh ở đây hoặc bằng method authorization. Nếu quên,
 * endpoint bị chặn thay vì mở — đó là hành vi mong muốn của deny-by-default
 * ({@code SEC-002}).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
                                                      JwtDecoder jwtDecoder,
                                                      JwtAuthenticationConverter jwtAuthenticationConverter)
            throws Exception {
        return http
                // API stateless dùng bearer token nên không có session và không có
                // CSRF token; tắt CSRF ở đây là an toàn vì không dùng cookie auth.
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        // Probe của orchestrator: liveness không phụ thuộc downstream
                        // (OPS-003) nên phải mở, ngược lại pod bị restart oan.
                        .requestMatchers("/actuator/health/liveness", "/actuator/health/readiness").permitAll()
                        // Endpoint quản trị còn lại chỉ mở trong management network,
                        // không mở qua đường request thông thường (SRD 11.2).
                        .requestMatchers("/actuator/**").denyAll()
                        .anyRequest().denyAll())
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)))
                // Thiếu/sai token trả 401 rỗng, không redirect tới trang login.
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .build();
    }

    /**
     * Decoder kiểm tra chữ ký theo JWKS, cộng thêm validator thời hạn, issuer và
     * audience. Cả ba đều bắt buộc; bỏ bất kỳ cái nào cũng tạo lỗ bypass.
     */
    @Bean
    public JwtDecoder jwtDecoder(MoneyBankSecurityProperties properties) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri(properties.getJwkSetUri())
                .build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<Jwt>(
                new JwtTimestampValidator(Duration.ofSeconds(properties.getClockSkewSeconds())),
                new JwtIssuerValidator(properties.getIssuer()),
                new JwtAudienceValidator(properties.getAudience())));
        return decoder;
    }

    /**
     * Chuyển scope trong token thành authority {@code SCOPE_*} để dùng với method
     * authorization. Scope là điều kiện cần; ownership và thuộc tính nghiệp vụ
     * được kiểm tra riêng ở application service ({@code AD-SEC-A05}).
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(MoneyBankSecurityProperties properties) {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("SCOPE_");
        authoritiesConverter.setAuthoritiesClaimName(properties.getScopeClaim());

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}
