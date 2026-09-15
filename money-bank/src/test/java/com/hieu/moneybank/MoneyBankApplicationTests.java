package com.hieu.moneybank;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import static org.mockito.Mockito.mock;

/**
 * Smoke test cho việc khởi tạo context.
 *
 * <p>Test này bắt các lỗi cấu hình không hiện ra khi compile: bean thiếu, cấu
 * hình security sai, {@code @ConfigurationProperties} không được bind.
 */
@SpringBootTest
@ActiveProfiles("test")
class MoneyBankApplicationTests {

    @Configuration
    static class TestConfig {
        @Bean
        public JwtDecoder jwtDecoder() {
            return mock(JwtDecoder.class);
        }
    }

    @Test
    void contextLoads() {
    }

}
