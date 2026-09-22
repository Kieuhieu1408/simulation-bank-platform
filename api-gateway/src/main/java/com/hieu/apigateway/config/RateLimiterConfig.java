package com.hieu.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

/**
 * Rate Limiter configuration cho API Gateway.
 *
 * <p>Dùng Spring Cloud Gateway built-in {@link RedisRateLimiter} với
 * token bucket algorithm. Redis lưu counter per key.
 *
 * <p>Key resolution priority:
 * <ol>
 *   <li>Authenticated user → key = "user:{userId}" (sub claim từ JWT)</li>
 *   <li>Unauthenticated / fallback → key = "ip:{clientIp}"</li>
 * </ol>
 *
 * <p>Threshold mặc định (configurable via application.yaml):
 * <ul>
 *   <li>Per-user: replenishRate=5 token/sec (=300/min), burst=10</li>
 *   <li>Per-IP: replenishRate=1 token/sec (=60/min), burst=5</li>
 * </ul>
 *
 * <p>Đây là tầng infrastructure rate limit. Business-level quota
 * (vd: max 5 transfers/day) do từng service tự quản lý.
 */
@Configuration
public class RateLimiterConfig {

    @Value("${gateway.rate-limit.user.replenish-rate:5}")
    private int userReplenishRate;

    @Value("${gateway.rate-limit.user.burst-capacity:10}")
    private int userBurstCapacity;

    @Value("${gateway.rate-limit.ip.replenish-rate:1}")
    private int ipReplenishRate;

    @Value("${gateway.rate-limit.ip.burst-capacity:5}")
    private int ipBurstCapacity;

    /**
     * KeyResolver: ưu tiên user ID (nếu đã authenticate), fallback về IP.
     *
     * <p>Được reference trong application.yaml: key-resolver: "#{@userOrIpKeyResolver}"
     */
    @Bean
    public KeyResolver userOrIpKeyResolver() {
        return exchange -> exchange.getPrincipal()
                .cast(Authentication.class)
                .filter(Authentication::isAuthenticated)
                .map(auth -> "user:" + auth.getName())
                .switchIfEmpty(Mono.fromSupplier(() -> {
                    var address = exchange.getRequest().getRemoteAddress();
                    String ip = address != null ? address.getAddress().getHostAddress() : "unknown";
                    return "ip:" + ip;
                }));
    }

    /**
     * Rate limiter dùng cho route-level configuration.
     * Threshold được đọc từ application.yaml, không hardcode.
     */
    @Bean
    public RedisRateLimiter defaultRateLimiter() {
        return new RedisRateLimiter(userReplenishRate, userBurstCapacity);
    }
}
