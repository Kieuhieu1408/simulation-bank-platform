package com.hieu.common.outbox;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "money-bank.outbox")
public class OutboxProperties {

    /**
     * Bật tiến trình đẩy event. Mặc định tắt vì chưa có adapter broker được chốt;
     * bật mà thiếu {@link OutboxMessagePublisher} thì ứng dụng fail khi khởi động.
     */
    private boolean dispatcherEnabled = false;

    /** Số event xử lý mỗi vòng. Giới hạn để một vòng không giữ connection quá lâu. */
    private int batchSize = 50;

    /** Khoảng nghỉ giữa hai vòng quét. */
    private Duration pollInterval = Duration.ofSeconds(1);

    /** Backoff cơ sở khi publish lỗi. Lần thử thứ n chờ {@code base * 2^(n-1)} cộng jitter. */
    private Duration retryBackoff = Duration.ofSeconds(2);

    /** Giới hạn trên của backoff để event không bị đẩy vô hạn về tương lai. */
    private Duration maxRetryBackoff = Duration.ofMinutes(5);

    /**
     * Số lần thử trước khi cảnh báo. Không xóa event sau ngưỡng này: mất event
     * nghiệp vụ nguy hiểm hơn là để nó tồn đọng và tạo alert.
     */
    private int alertAfterAttempts = 10;

    public boolean isDispatcherEnabled() {
        return dispatcherEnabled;
    }

    public void setDispatcherEnabled(boolean dispatcherEnabled) {
        this.dispatcherEnabled = dispatcherEnabled;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public Duration getPollInterval() {
        return pollInterval;
    }

    public void setPollInterval(Duration pollInterval) {
        this.pollInterval = pollInterval;
    }

    public Duration getRetryBackoff() {
        return retryBackoff;
    }

    public void setRetryBackoff(Duration retryBackoff) {
        this.retryBackoff = retryBackoff;
    }

    public Duration getMaxRetryBackoff() {
        return maxRetryBackoff;
    }

    public void setMaxRetryBackoff(Duration maxRetryBackoff) {
        this.maxRetryBackoff = maxRetryBackoff;
    }

    public int getAlertAfterAttempts() {
        return alertAfterAttempts;
    }

    public void setAlertAfterAttempts(int alertAfterAttempts) {
        this.alertAfterAttempts = alertAfterAttempts;
    }
}
