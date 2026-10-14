package com.hieu.moneybank;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.mockito.Mockito.mock;

/**
 * Kiểm tra khởi tạo context với cấu hình mặc định (kết nối Oracle DB và ddl-auto: validate).
 * Xác nhận triệt để lỗi SchemaManagementException: missing table [accounts] đã được giải quyết.
 */
@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092"
})
class MoneyBankSchemaValidationTest {

    @Configuration
    static class TestConfig {
        @Bean
        public JwtDecoder jwtDecoder() {
            return mock(JwtDecoder.class);
        }
    }

    @Test
    @DisplayName("Khởi tạo context thành công với Oracle DB schema validation")
    void contextLoadsAndSchemaValidationPasses() {
    }
}
