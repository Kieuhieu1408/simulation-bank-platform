package com.hieu.moneybank.shared.security;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.hieu.moneybank.shared.error.AuthenticationRequiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Dựng {@link IdentityContext} từ token đã được resource server xác minh.
 *
 * <p>Fail closed ở mọi nhánh thiếu dữ liệu ({@code AD-SEC-P05}): không có
 * authentication, sai loại token, thiếu {@code sub} hoặc thiếu claim khách hàng
 * đều ném exception. Không có giá trị mặc định, không có "anonymous customer".
 */
@Component
public class IdentityContextResolver {

    private final MoneyBankSecurityProperties properties;

    public IdentityContextResolver(MoneyBankSecurityProperties properties) {
        this.properties = properties;
    }

    /**
     * @throws AuthenticationRequiredException nếu request không có định danh
     *         người dùng đã xác minh
     */
    public IdentityContext requireCurrent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationRequiredException("Không có authentication trong SecurityContext");
        }
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            // Ví dụ token client credentials hoặc authentication type khác: không
            // đại diện người dùng cuối nên không được dùng cho use case khách hàng.
            throw new AuthenticationRequiredException(
                    "Authentication không phải JWT người dùng: " + authentication.getClass().getSimpleName());
        }

        Jwt jwt = jwtAuthentication.getToken();
        String subject = jwt.getSubject();
        if (!StringUtils.hasText(subject)) {
            throw new AuthenticationRequiredException("Token thiếu claim sub");
        }
        String customerId = jwt.getClaimAsString(properties.getCustomerClaim());
        if (!StringUtils.hasText(customerId)) {
            throw new AuthenticationRequiredException(
                    "Token thiếu claim khách hàng: " + properties.getCustomerClaim());
        }

        return new IdentityContext(subject, customerId, extractScopes(jwt), jwt.getClaimAsString("sid"));
    }

    private Set<String> extractScopes(Jwt jwt) {
        String rawScopes = jwt.getClaimAsString(properties.getScopeClaim());
        if (!StringUtils.hasText(rawScopes)) {
            return Set.of();
        }
        // Keycloak trả scope dạng chuỗi cách nhau bởi space theo RFC 6749.
        return new LinkedHashSet<>(Arrays.asList(rawScopes.trim().split("\\s+")));
    }
}
