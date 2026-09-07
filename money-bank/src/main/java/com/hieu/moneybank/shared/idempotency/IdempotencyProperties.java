package com.hieu.moneybank.shared.idempotency;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Cấu hình idempotency.
 *
 * <p>{@code resultRetention} phải lớn hơn cửa sổ retry của client, nếu không một
 * lần retry muộn sẽ được coi là ý định mới và tạo giao dịch thứ hai. Giá trị khởi
 * điểm 24 giờ theo SRD mục 6.2 và còn chờ nghiệp vụ chốt.
 */
@ConfigurationProperties(prefix = "money-bank.idempotency")
public class IdempotencyProperties {

    private Duration resultRetention = Duration.ofHours(24);

    /**
     * Số lần đọc lại khi insert thất bại vì trùng khóa. Thông thường một lần là
     * đủ vì transaction thắng đã commit trước khi index nhả khóa; giới hạn nhỏ ở
     * đây chỉ để không loop vô hạn nếu gặp trạng thái bất thường.
     */
    private int duplicateReadAttempts = 3;

    public Duration getResultRetention() {
        return resultRetention;
    }

    public void setResultRetention(Duration resultRetention) {
        this.resultRetention = resultRetention;
    }

    public int getDuplicateReadAttempts() {
        return duplicateReadAttempts;
    }

    public void setDuplicateReadAttempts(int duplicateReadAttempts) {
        this.duplicateReadAttempts = duplicateReadAttempts;
    }
}
