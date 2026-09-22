package com.hieu.common.idempotency;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ghi bản ghi claim trong một transaction riêng (TX-A theo
 * {@code data-and-consistency-design.md} mục 2.2).
 *
 * <p>Tách thành bean riêng, không gộp vào {@link IdempotencyService}, vì:
 * <ul>
 *   <li>{@code REQUIRES_NEW} chỉ có hiệu lực khi gọi qua proxy Spring; gọi nội bộ
 *       trong cùng bean sẽ bỏ qua annotation ({@code AD-SEC-A02} cũng dựa trên
 *       nguyên tắc này).</li>
 *   <li>Khi insert vi phạm unique constraint, transaction đó bắt buộc phải
 *       rollback. Việc đọc lại phải diễn ra ở transaction khác, nên ranh giới
 *       transaction phải rõ ràng chứ không thể để chung.</li>
 * </ul>
 */
@Component
public class IdempotencyRecordWriter {

    private final IdempotencyRecordRepository repository;
    private final IdempotencyProperties properties;
    private final Clock clock;

    public IdempotencyRecordWriter(IdempotencyRecordRepository repository,
                                   IdempotencyProperties properties,
                                   Clock clock) {
        this.repository = repository;
        this.properties = properties;
        this.clock = clock;
    }

    /**
     * Insert claim và flush ngay để unique constraint được kiểm tra trong phạm vi
     * transaction này.
     *
     * <p>Không dùng {@code save} thuần: Hibernate có thể hoãn insert tới lúc commit,
     * khiến exception nổ ra ở nơi khó xử lý. {@code saveAndFlush} đưa lỗi về đúng
     * điểm gọi ({@code AD-DC-C01}).
     *
     * @throws org.springframework.dao.DataIntegrityViolationException nếu đã có
     *         claim cho cùng {@code (customerId, operation, idempotencyKey)}
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public IdempotencyRecord insertClaim(String customerId,
                                         IdempotencyOperation operation,
                                         String idempotencyKey,
                                         String requestHash,
                                         String transactionId,
                                         String workflowId) {
        Instant now = clock.instant();
        IdempotencyRecord record = IdempotencyRecord.claim(
                customerId,
                operation,
                idempotencyKey,
                requestHash,
                transactionId,
                workflowId,
                now,
                now.plus(properties.getResultRetention()));
        return repository.saveAndFlush(record);
    }

    /**
     * Chốt kết quả cuối cho một claim.
     *
     * <p>Trong luồng chuyển tiền thật, việc chốt này phải nằm trong cùng
     * transaction với thay đổi trạng thái giao dịch và bản ghi outbox
     * ({@code AD-TR-R09}). Vì vậy mặc định dùng {@code REQUIRED} để tham gia
     * transaction của use case đang gọi, không mở transaction mới.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void settle(Long recordId,
                       IdempotencyStatus terminalStatus,
                       String responseCode,
                       String responseSnapshot) {
        Optional<IdempotencyRecord> found = repository.findById(recordId);
        IdempotencyRecord record = found.orElseThrow(() ->
                new IllegalStateException("Không tìm thấy idempotency record id=" + recordId));
        record.settle(terminalStatus, responseCode, responseSnapshot, clock.instant());
        repository.save(record);
    }
}
