package com.hieu.moneybank.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Cấu hình kết nối tới Corebank (prefix {@code corebank}).
 *
 * <p>Mọi timeout lấy từ yaml, không hardcode. Thứ tự hợp lý:
 * {@code connect-timeout < response-timeout < total-timeout}. {@code total-timeout}
 * là chốt chặn cuối cho toàn bộ lời gọi (gồm cả bước lấy token client-credentials),
 * để request thread không bao giờ bị treo vô hạn.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "corebank")
public class CorebankProperties {

    /** Base URL của Corebank. */
    private String url = "http://localhost:8180";

    /** Thời gian tối đa để thiết lập TCP connection. */
    private Duration connectTimeout = Duration.ofSeconds(2);

    /** Thời gian tối đa chờ response sau khi đã gửi request. */
    private Duration responseTimeout = Duration.ofSeconds(10);

    /** Thời gian tối đa cho toàn bộ lời gọi, kể cả lấy token. */
    private Duration totalTimeout = Duration.ofSeconds(15);
}
