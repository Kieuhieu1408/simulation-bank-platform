package com.hieu.common.security;

import java.util.List;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Bắt buộc claim {@code aud} chứa audience của service này.
 *
 * <p>Đây là kiểm soát chặn bypass Gateway và chặn dùng lại token của service
 * khác ({@code AD-SEC-P02}, {@code AD-SEC-TE01}). Không có validator này thì một
 * token hợp lệ phát cho {@code profile-service} vẫn gọi được Money Bank.
 *
 * <p>Token không có {@code aud} bị từ chối: deny-by-default, không suy luận
 * audience từ issuer.
 */
public class JwtAudienceValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error INVALID_AUDIENCE = new OAuth2Error(
            "invalid_token",
            "Token audience không hợp lệ",
            null);

    private final String requiredAudience;

    public JwtAudienceValidator(String requiredAudience) {
        this.requiredAudience = requiredAudience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        List<String> audiences = token.getAudience();
        if (audiences != null && audiences.contains(requiredAudience)) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(INVALID_AUDIENCE);
    }
}
