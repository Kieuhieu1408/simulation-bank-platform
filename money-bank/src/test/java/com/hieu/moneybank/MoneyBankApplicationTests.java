package com.hieu.moneybank;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test cho việc khởi tạo context.
 *
 * <p>Test này bắt các lỗi cấu hình không hiện ra khi compile: bean thiếu, cấu
 * hình security sai, {@code @ConfigurationProperties} không được bind.
 */
@SpringBootTest
@ActiveProfiles("test")
class MoneyBankApplicationTests {

    @Test
    void contextLoads() {
    }

}
