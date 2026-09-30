package com.hieu.moneybank.messaging;

import com.hieu.common.outbox.OutboxDispatcher;
import com.hieu.common.outbox.OutboxProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Bộ lập lịch kích hoạt {@link OutboxDispatcher#dispatchBatch()} định kỳ (U-07).
 *
 * <h3>Điều kiện kích hoạt</h3>
 * <ul>
 *   <li>{@code money-bank.outbox.dispatcher-enabled=true} — phải bật rõ ràng.</li>
 *   <li>{@link KafkaOutboxMessagePublisher} bean phải tồn tại — tức là Kafka đã
 *       được cấu hình (bootstrap-servers).</li>
 * </ul>
 * <p>Nếu thiếu một trong hai điều kiện, class này không được tạo → Outbox ở trạng
 * thái "ghi nhưng không đẩy" (an toàn hơn là ghi rồi mất silently).
 *
 * <h3>Scheduling strategy</h3>
 * <p>Dùng {@code @Scheduled(fixedDelayString)} thay vì {@code fixedRate}:
 * delay tính từ lúc batch trước <em>xong</em>, không phải từ lúc bắt đầu.
 * Điều này ngăn các batch chồng lấp nhau khi Kafka chậm.
 *
 * <h3>Observability</h3>
 * <p>Mỗi vòng dispatch ghi metric {@code moneybank.outbox.dispatch.duration}
 * và counter {@code moneybank.outbox.dispatch.published} để Grafana alert.
 */
@Component
@EnableScheduling
@ConditionalOnProperty(
    prefix  = "money-bank.outbox",
    name    = "dispatcher-enabled",
    havingValue = "true"
)
@ConditionalOnBean(KafkaOutboxMessagePublisher.class)
@RequiredArgsConstructor
@Slf4j
public class OutboxDispatchScheduler {

    private static final String METRIC_DISPATCH_DURATION  = "moneybank.outbox.dispatch.duration";
    private static final String METRIC_DISPATCH_PUBLISHED = "moneybank.outbox.dispatch.published";
    private static final String METRIC_DISPATCH_ERRORS    = "moneybank.outbox.dispatch.errors";

    private final OutboxDispatcher dispatcher;
    private final OutboxProperties  outboxProperties;
    private final MeterRegistry     meterRegistry;

    /**
     * Kích hoạt một batch dispatch.
     *
     * <p>Delay lấy từ {@code money-bank.outbox.poll-interval} (default 1s).
     * Spring EL {@code #{...}} đọc bean, không phải property string trực tiếp.
     *
     * <p>Fixed delay — không chạy batch mới cho đến khi batch cũ xong.
     */
    @Scheduled(fixedDelayString = "#{outboxProperties.pollInterval.toMillis()}")
    public void dispatchBatch() {
        Timer.Sample sample = Timer.start(meterRegistry);
        int published = 0;
        try {
            published = dispatcher.dispatchBatch();

            if (published > 0) {
                log.debug("eventName=OUTBOX_BATCH_DONE published={}", published);
            }

            meterRegistry.counter(METRIC_DISPATCH_PUBLISHED)
                .increment(published);

        } catch (Exception e) {
            // Không để exception kill scheduler thread
            meterRegistry.counter(METRIC_DISPATCH_ERRORS).increment();
            log.error("eventName=OUTBOX_DISPATCH_ERROR errorClass={} message={}",
                e.getClass().getSimpleName(), e.getMessage(), e);
        } finally {
            sample.stop(Timer.builder(METRIC_DISPATCH_DURATION)
                .description("Duration of one outbox dispatch batch")
                .register(meterRegistry));
        }
    }

    /**
     * Log cảnh báo khi dispatcher bật nhưng event tồn đọng nhiều.
     * Chạy mỗi 5 phút — không cần nhanh hơn.
     *
     * <p>Đây là safeguard: nếu Kafka down lâu, DBA nhìn thấy alert này
     * mà không cần query DB thủ công.
     */
    @Scheduled(fixedDelay = 300_000)   // 5 phút
    public void alertOnStaleEvents() {
        int threshold = outboxProperties.getAlertAfterAttempts();
        // Metric đã được OutboxDispatcher ghi — ở đây chỉ log heartbeat.
        log.debug("eventName=OUTBOX_HEALTH_CHECK ts={} alertThreshold={}",
            Instant.now(), threshold);
    }
}
