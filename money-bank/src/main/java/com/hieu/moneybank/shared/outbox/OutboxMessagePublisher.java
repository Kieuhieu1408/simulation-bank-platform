package com.hieu.moneybank.shared.outbox;

/**
 * Port đẩy message ra broker.
 *
 * <p>Cố tình chưa có implementation Kafka trong repo này: tên topic, format
 * serialization và compatibility policy chưa được chốt
 * ({@code AD-SC-OPEN-03}, {@code OQ-P1-12}). Cũng cố tình không cung cấp
 * implementation mặc định kiểu "log rồi coi như đã gửi" — một fake như vậy sẽ đánh
 * dấu event là published trong khi không ai nhận được, và sự cố chỉ lộ ra ở
 * production.
 *
 * <p>Vì vậy khi bật dispatcher mà thiếu bean này, ứng dụng sẽ fail lúc khởi động.
 * Đó là hành vi mong muốn.
 */
public interface OutboxMessagePublisher {

    /**
     * Đẩy một message. Chỉ trả về bình thường khi broker đã nhận.
     *
     * @throws RuntimeException nếu chưa chắc broker đã nhận; dispatcher sẽ giữ
     *         event để retry thay vì đánh dấu published
     */
    void publish(OutboxMessage message);
}
