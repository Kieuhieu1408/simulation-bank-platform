package com.hieu.moneybank.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Beans hạ tầng chung: Clock và ObjectMapper.
 *
 * <p>Clock được dùng bởi OutboxRecorder và các service cần thời gian
 * có thể mock trong test.
 *
 * <p>ObjectMapper được dùng bởi ConfirmTransferProposalCommandHandler
 * để serialize Outbox payload.
 */
@Configuration
public class InfrastructureConfig {

    /**
     * Clock UTC — inject thay vì gọi Instant.now() trực tiếp để test có thể mock.
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    /**
     * ObjectMapper cấu hình sẵn cho Java 8 date/time.
     * Spring Boot tự tạo bean này nhưng khai báo rõ để inject theo tên nếu cần.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
