package com.hieu.common.security;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Cấu hình xác minh token của Money Bank ({@code SEC-001}, {@code AD-SEC-P02}).
 *
 * <p>Không có giá trị mặc định cho {@code issuer}, {@code audience} và
 * {@code jwkSetUri}: thiếu cấu hình thì ứng dụng phải fail lúc khởi động, chứ
 * không được chạy với validator rỗng rồi chấp nhận mọi token.
 *
 * <p>Dùng {@code jwkSetUri} thay vì issuer discovery để việc khởi động không phụ
 * thuộc Keycloak đang sống; JWK chỉ được tải khi cần xác minh token đầu tiên.
 */
@Validated
@Component
@ConfigurationProperties(prefix = "money-bank.security")
public class CommonSecurityProperties {

    /** Endpoint JWKS của Keycloak realm. */
    @NotBlank
    private String jwkSetUri;

    /** Issuer bắt buộc phải khớp claim {@code iss}. */
    @NotBlank
    private String issuer;

    /**
     * Audience mà service này chấp nhận. Token phát cho service khác phải bị từ
     * chối, kể cả khi cùng issuer ({@code AD-SEC-TE01}).
     */
    @NotBlank
    private String audience;

    /** Claim chứa scope. Keycloak mặc định dùng {@code scope} dạng chuỗi cách nhau bởi space. */
    private String scopeClaim = "scope";

    /**
     * Claim mang định danh khách hàng. Ownership luôn lấy từ claim này, không bao
     * giờ từ body/query ({@code AD-SEC-A05}).
     */
    private String customerClaim = "customer_id";

    /** Dung sai lệch giờ khi kiểm tra {@code exp}/{@code nbf}, đơn vị giây. */
    private long clockSkewSeconds = 30;

    public String getJwkSetUri() {
        return jwkSetUri;
    }

    public void setJwkSetUri(String jwkSetUri) {
        this.jwkSetUri = jwkSetUri;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getScopeClaim() {
        return scopeClaim;
    }

    public void setScopeClaim(String scopeClaim) {
        this.scopeClaim = scopeClaim;
    }

    public String getCustomerClaim() {
        return customerClaim;
    }

    public void setCustomerClaim(String customerClaim) {
        this.customerClaim = customerClaim;
    }

    public long getClockSkewSeconds() {
        return clockSkewSeconds;
    }

    public void setClockSkewSeconds(long clockSkewSeconds) {
        this.clockSkewSeconds = clockSkewSeconds;
    }
}
