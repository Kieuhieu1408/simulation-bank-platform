package com.hieu.common.outbox;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

/**
 * Bản ghi Outbox ({@code ADR-08}, {@code AD-DC-O01}).
 *
 * <p>Vì sao cần bảng này thay vì publish Kafka trực tiếp trong use case: ghi
 * database và gửi message là hai hệ thống khác nhau, không có transaction chung.
 * Publish trước khi commit thì có thể phát event cho giao dịch bị rollback;
 * publish sau commit thì process chết giữa hai bước sẽ mất event. Outbox loại bỏ
 * dual-write bằng cách ghi event vào cùng transaction với state change, rồi để
 * một tiến trình riêng đẩy đi.
 *
 * <p>Hệ quả phải chấp nhận: giao hàng là at-least-once. Consumer bắt buộc phải
 * idempotent ({@code AD-DC-O02}).
 */
@Entity
@Table(
        name = "outbox_event",
        uniqueConstraints = @UniqueConstraint(name = "uk_outbox_event_id", columnNames = "event_id"),
        indexes = {
                @Index(name = "ix_outbox_pending", columnList = "published_at, next_attempt_at"),
                @Index(name = "ix_outbox_aggregate", columnList = "aggregate_type, aggregate_id")
        })
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /** Định danh nghiệp vụ của event, là khóa để consumer chống trùng. */
    @Column(name = "event_id", nullable = false, updatable = false, length = 64)
    private String eventId;

    @Column(name = "aggregate_type", nullable = false, updatable = false, length = 64)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, updatable = false, length = 64)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, updatable = false, length = 128)
    private String eventType;

    /** Cho phép consumer cũ và mới cùng tồn tại trong cửa sổ rollout ({@code AD-SC-V03}). */
    @Column(name = "schema_version", nullable = false, updatable = false, length = 16)
    private String schemaVersion;

    /**
     * Payload đã sanitize. Không chứa full account number, token hay dữ liệu xác
     * thực ({@code AD-DC-O06}): event đi ra ngoài phạm vi service và tồn tại trong
     * Kafka theo retention.
     */
    @Lob
    @Column(name = "payload", nullable = false, updatable = false)
    private String payload;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    /** {@code null} nghĩa là chưa publish. Dùng làm điều kiện quét. */
    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    /** Mã lỗi an toàn của lần thử gần nhất. Không lưu stack trace. */
    @Column(name = "last_error_code", length = 64)
    private String lastErrorCode;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected OutboxEvent() {
        // Bắt buộc cho JPA.
    }

    private OutboxEvent(String eventId,
                        String aggregateType,
                        String aggregateId,
                        String eventType,
                        String schemaVersion,
                        String payload,
                        Instant occurredAt) {
        this.eventId = eventId;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.schemaVersion = schemaVersion;
        this.payload = payload;
        this.occurredAt = occurredAt;
        this.attemptCount = 0;
        this.nextAttemptAt = occurredAt;
    }

    static OutboxEvent pending(String eventId,
                               String aggregateType,
                               String aggregateId,
                               String eventType,
                               String schemaVersion,
                               String payload,
                               Instant occurredAt) {
        return new OutboxEvent(eventId, aggregateType, aggregateId, eventType,
                schemaVersion, payload, occurredAt);
    }

    /**
     * Bắt đầu một lần thử publish và đặt lease tới {@code leaseUntil}.
     *
     * <p>Lease đẩy {@code nextAttemptAt} về tương lai nên worker khác quét cùng
     * lúc sẽ không lấy lại event này. Kết hợp với {@code @Version}, hai worker
     * không thể cùng lease một event: kẻ chậm hơn nhận
     * {@code OptimisticLockingFailureException}.
     *
     * <p>Lease chỉ giảm số lần publish trùng, không loại bỏ được: nếu process chết
     * sau khi broker đã nhận nhưng trước khi commit trạng thái, event sẽ được gửi
     * lại. Đó là lý do consumer bắt buộc idempotent ({@code AD-DC-O02}).
     */
    void startAttempt(Instant leaseUntil) {
        this.attemptCount = this.attemptCount + 1;
        this.nextAttemptAt = leaseUntil;
    }

    void markPublished(Instant now) {
        this.publishedAt = now;
        this.lastErrorCode = null;
    }

    /**
     * Lùi lần thử kế tiếp sau khi publish thất bại.
     *
     * <p>Không đánh dấu published và không xóa bản ghi: event phải được giữ để
     * retry. Nếu broker hoặc audit projector lỗi, giao dịch tài chính đã được
     * Corebank xác nhận vẫn không bị rollback ({@code AD-DC-TX05}).
     */
    void scheduleRetry(Instant nextAttemptAt, String safeErrorCode) {
        this.nextAttemptAt = nextAttemptAt;
        this.lastErrorCode = safeErrorCode;
    }

    public Long id() {
        return id;
    }

    public String eventId() {
        return eventId;
    }

    public String aggregateType() {
        return aggregateType;
    }

    public String aggregateId() {
        return aggregateId;
    }

    public String eventType() {
        return eventType;
    }

    public String schemaVersion() {
        return schemaVersion;
    }

    public String payload() {
        return payload;
    }

    public Instant occurredAt() {
        return occurredAt;
    }

    public Instant publishedAt() {
        return publishedAt;
    }

    public int attemptCount() {
        return attemptCount;
    }

    public Instant nextAttemptAt() {
        return nextAttemptAt;
    }

    public String lastErrorCode() {
        return lastErrorCode;
    }

    public boolean published() {
        return publishedAt != null;
    }

    public long version() {
        return version;
    }
}
