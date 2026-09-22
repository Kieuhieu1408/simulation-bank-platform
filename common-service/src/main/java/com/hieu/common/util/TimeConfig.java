package com.hieu.common.util;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Nguồn thời gian dùng chung.
 *
 * <p>Luôn UTC theo SRD mục 1.1 và {@code AD-DC-D02}. Inject {@link Clock} thay vì
 * gọi {@code Instant.now()} rải rác để test có thể cố định thời gian khi kiểm tra
 * expiry, backoff và lease — những hành vi không thể kiểm chứng nếu phụ thuộc
 * đồng hồ hệ thống.
 */
@Configuration
public class TimeConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
