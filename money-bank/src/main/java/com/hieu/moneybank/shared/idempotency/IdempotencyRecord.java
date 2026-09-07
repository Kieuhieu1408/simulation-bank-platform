package com.hieu.moneybank.shared.idempotency;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

/**
 * Bản ghi idempotency bền vững — lớp an toàn cuối cùng chống xử lý lặp
 * ({@code ADR-03}, {@code DV-DEC-10}, {@code AD-DC-C02}).
 *
 * <p>Unique constraint {@code uk_idem_customer_operation_key} là cơ chế quyết
 * định, không phải câu {@code SELECT} kiểm tra trước. Redis chỉ là fast-path và
 * có thể mất dữ liệu; database mới là nơi cưỡng chế bất biến "một ý định tạo tối
 * đa một giao dịch" ({@code TRF-INV-006}).
 */
@Entity
@Table(
        name = "idempotency_record",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_idem_customer_operation_key",
                        columnNames = {"customer_id", "operation", "idempotency_key"}),
                @UniqueConstraint(
                        name = "uk_idem_transaction_id",
                        columnNames = {"transaction_id"})
        },
        indexes = {
                @Index(name = "ix_idem_workflow_id", columnList = "workflow_id"),
                @Index(name = "ix_idem_expires_at", columnList = "expires_at")
        })
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "customer_id", nullable = false, updatable = false, length = 64)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", nullable = false, updatable = false, length = 64)
    private IdempotencyOperation operation;

    @Column(name = "idempotency_key", nullable = false, updatable = false, length = 64)
    private String idempotencyKey;

    /**
     * Immutable sau khi claim ({@code AD-DC-C06}). Cho phép sửa hash là mở đường
     * cho một request khác dữ liệu chiếm lại key đã dùng.
     */
    @Column(name = "request_hash", nullable = false, updatable = false, length = CanonicalRequestHasher.HASH_LENGTH)
    private String requestHash;

    @Column(name = "transaction_id", updatable = false, length = 64)
    private String transactionId;

    @Column(name = "workflow_id", updatable = false, length = 64)
    private String workflowId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private IdempotencyStatus status;

    /** Mã lỗi/kết quả đã ánh xạ, dùng để trả lại nguyên trạng cho request trùng. */
    @Column(name = "response_code", length = 32)
    private String responseCode;

    /**
     * Response đã sanitize để trả lại cho request trùng ({@code AD-DC-C07}).
     * Không chứa dữ liệu xác thực, token hay full account number.
     */
    @Column(name = "response_snapshot", length = 4000)
    private String responseSnapshot;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Mốc hết hiệu lực cho việc trả lại kết quả cũ. Không phải lệnh xóa: bản ghi
     * tài chính chỉ được purge/archive theo retention policy đã phê duyệt
     * ({@code AD-DC-D06}).
     */
    @Column(name = "expires_at")
    private Instant expiresAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected IdempotencyRecord() {
        // Bắt buộc cho JPA.
    }

    private IdempotencyRecord(String customerId,
                             IdempotencyOperation operation,
                             String idempotencyKey,
                             String requestHash,
                             String transactionId,
                             String workflowId,
                             Instant now,
                             Instant expiresAt) {
        this.customerId = customerId;
        this.operation = operation;
        this.idempotencyKey = idempotencyKey;
        this.requestHash = requestHash;
        this.transactionId = transactionId;
        this.workflowId = workflowId;
        this.status = IdempotencyStatus.IN_PROGRESS;
        this.createdAt = now;
        this.updatedAt = now;
        this.expiresAt = expiresAt;
    }

    /**
     * Tạo bản ghi claim mới ở trạng thái {@link IdempotencyStatus#IN_PROGRESS}.
     *
     * <p>{@code transactionId} được sinh trước khi insert nhưng chỉ có hiệu lực
     * nếu insert thành công; request thua cuộc sẽ rollback và không bao giờ dùng
     * id đó ({@code ADR-04}, {@code AD-TR-R06}).
     */
    static IdempotencyRecord claim(String customerId,
                                   IdempotencyOperation operation,
                                   String idempotencyKey,
                                   String requestHash,
                                   String transactionId,
                                   String workflowId,
                                   Instant now,
                                   Instant expiresAt) {
        return new IdempotencyRecord(customerId, operation, idempotencyKey, requestHash,
                transactionId, workflowId, now, expiresAt);
    }

    /** Chốt kết quả cuối. Không cho phép quay lại {@code IN_PROGRESS}. */
    void settle(IdempotencyStatus terminalStatus, String responseCode, String responseSnapshot, Instant now) {
        if (!terminalStatus.isTerminal()) {
            throw new IllegalArgumentException("Trạng thái chốt phải là terminal: " + terminalStatus);
        }
        this.status = terminalStatus;
        this.responseCode = responseCode;
        this.responseSnapshot = responseSnapshot;
        this.updatedAt = now;
    }

    public Long id() {
        return id;
    }

    public String customerId() {
        return customerId;
    }

    public IdempotencyOperation operation() {
        return operation;
    }

    public String idempotencyKey() {
        return idempotencyKey;
    }

    public String requestHash() {
        return requestHash;
    }

    public String transactionId() {
        return transactionId;
    }

    public String workflowId() {
        return workflowId;
    }

    public IdempotencyStatus status() {
        return status;
    }

    public String responseCode() {
        return responseCode;
    }

    public String responseSnapshot() {
        return responseSnapshot;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public boolean matchesHash(String candidateHash) {
        return requestHash.equals(candidateHash);
    }
}
