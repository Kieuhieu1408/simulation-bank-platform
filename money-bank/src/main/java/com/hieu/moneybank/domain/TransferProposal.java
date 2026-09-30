package com.hieu.moneybank.domain;

import com.hieu.moneybank.constant.ProposalStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Proposal chuyển tiền — aggregate root của luồng Transfer U-06.
 *
 * <h3>State Machine</h3>
 * <pre>
 * PENDING ──confirm()──► VALIDATING ──corebank ok──► CONFIRMED
 *        └─reject()──►  REJECTED              └──corebank fail──► FAILED
 * </pre>
 *
 * <h3>Bất biến nghiệp vụ (TRF-INV-*)</h3>
 * <ul>
 *   <li>TRF-INV-001: amount > 0.</li>
 *   <li>TRF-INV-002: sourceAccountId ≠ destinationAccountId.</li>
 *   <li>TRF-INV-003: currency đúng ISO-4217 3 ký tự.</li>
 *   <li>TRF-INV-004: idempotencyKey không được null/blank.</li>
 *   <li>TRF-INV-005: confirm() chỉ được gọi khi trạng thái PENDING.</li>
 *   <li>TRF-INV-006: mỗi idempotencyKey chỉ được dùng cho một intent (UK DB).</li>
 * </ul>
 *
 * <p>Domain invariant được kiểm tra tại constructor và từng phương thức chuyển
 * trạng thái — không để lọt qua tầng service hay controller.
 */
@Getter
@Entity
@Table(
    name = "transfer_proposals",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_proposal_idempotency_key",
        columnNames = "idempotency_key"
    ),
    indexes = {
        @Index(name = "idx_proposal_source",    columnList = "source_account_id, created_at"),
        @Index(name = "idx_proposal_dest",      columnList = "destination_account_id, created_at"),
        @Index(name = "idx_proposal_status",    columnList = "status, created_at"),
        @Index(name = "idx_proposal_initiator", columnList = "initiator_customer_id, created_at")
    }
)
public class TransferProposal {

    @Id
    @Column(length = 36)
    private String id;

    /**
     * CIF của người khởi tạo — lấy từ identity context đã xác minh,
     * không từ request body. Dùng làm phạm vi idempotency key.
     */
    @Column(name = "initiator_customer_id", nullable = false, length = 64)
    private String initiatorCustomerId;

    @Column(name = "source_account_id", nullable = false, length = 36)
    private String sourceAccountId;

    @Column(name = "destination_account_id", nullable = false, length = 36)
    private String destinationAccountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "idempotency_key", nullable = false, length = 64)
    private String idempotencyKey;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProposalStatus status;

    /**
     * Transaction ID được set khi Corebank xác nhận thành công.
     * Null khi proposal chưa CONFIRMED.
     */
    @Column(name = "corebank_transaction_id", length = 64)
    private String corebankTransactionId;

    /**
     * Lý do từ chối/thất bại — mã an toàn, không stack trace.
     * Null khi proposal PENDING hoặc CONFIRMED.
     */
    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * OCC — chặn concurrent confirm cùng proposal.
     * Khi hai request cùng confirm(): kẻ chậm nhận OptimisticLockingFailureException.
     */
    @Version
    @Column(nullable = false)
    private long version;

    protected TransferProposal() {}

    /**
     * Tạo proposal mới ở trạng thái PENDING.
     *
     * <p>Tất cả bất biến được kiểm tra tại đây. Proposal chỉ hợp lệ khi qua được
     * constructor này.
     *
     * @param initiatorCustomerId CIF từ identity context — không từ body
     * @param sourceAccountId     account nguồn
     * @param destinationAccountId account đích
     * @param amount              số tiền > 0
     * @param currency            mã tiền tệ ISO-4217
     * @param idempotencyKey      key duy nhất do client tạo
     * @param description         mô tả giao dịch (nullable)
     */
    public TransferProposal(String initiatorCustomerId,
                            String sourceAccountId,
                            String destinationAccountId,
                            BigDecimal amount,
                            String currency,
                            String idempotencyKey,
                            String description) {
        // TRF-INV-002: source ≠ destination
        if (sourceAccountId.equals(destinationAccountId)) {
            throw new IllegalArgumentException("TRF-INV-002: sourceAccountId và destinationAccountId phải khác nhau");
        }
        // TRF-INV-001: amount > 0
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("TRF-INV-001: amount phải lớn hơn 0");
        }
        // TRF-INV-003: currency 3 ký tự
        if (currency == null || !currency.matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("TRF-INV-003: currency phải là mã ISO-4217 3 ký tự");
        }
        // TRF-INV-004: idempotencyKey không blank
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("TRF-INV-004: idempotencyKey không được để trống");
        }

        Instant now = Instant.now();
        this.id = UUID.randomUUID().toString();
        this.initiatorCustomerId = initiatorCustomerId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.currency = currency.toUpperCase();
        this.idempotencyKey = idempotencyKey;
        this.description = description;
        this.status = ProposalStatus.PENDING;
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Chuyển sang VALIDATING trước khi gọi Corebank.
     *
     * <p>TRF-INV-005: chỉ được gọi khi PENDING.
     * Kết hợp với @Version: hai request cùng confirm() sẽ chỉ có một người thắng.
     *
     * @throws IllegalStateException nếu proposal không ở trạng thái PENDING
     */
    public void startValidating() {
        if (!status.canConfirm()) {
            throw new IllegalStateException(
                "TRF-INV-005: Proposal [" + id + "] không thể xác nhận từ trạng thái " + status);
        }
        this.status = ProposalStatus.VALIDATING;
        this.updatedAt = Instant.now();
    }

    /**
     * Chốt thành công sau khi Corebank ghi sổ.
     *
     * @param corebankTransactionId ID giao dịch từ Corebank
     */
    public void markConfirmed(String corebankTransactionId) {
        if (this.status != ProposalStatus.VALIDATING) {
            throw new IllegalStateException(
                "markConfirmed() chỉ được gọi từ trạng thái VALIDATING, hiện tại: " + status);
        }
        this.status = ProposalStatus.CONFIRMED;
        this.corebankTransactionId = corebankTransactionId;
        this.updatedAt = Instant.now();
    }

    /**
     * Chốt thất bại sau khi Corebank từ chối hoặc lỗi nghiệp vụ.
     *
     * @param safeFailureReason mô tả lý do an toàn (không stack trace)
     */
    public void markFailed(String safeFailureReason) {
        if (this.status != ProposalStatus.VALIDATING) {
            throw new IllegalStateException(
                "markFailed() chỉ được gọi từ trạng thái VALIDATING, hiện tại: " + status);
        }
        this.status = ProposalStatus.FAILED;
        this.failureReason = safeFailureReason;
        this.updatedAt = Instant.now();
    }

    /**
     * Từ chối proposal trước khi gọi Corebank (vi phạm domain rule ở tầng validation).
     *
     * @param safeReason lý do từ chối an toàn
     */
    public void reject(String safeReason) {
        if (!status.canReject()) {
            throw new IllegalStateException(
                "reject() chỉ được gọi khi PENDING, hiện tại: " + status);
        }
        this.status = ProposalStatus.REJECTED;
        this.failureReason = safeReason;
        this.updatedAt = Instant.now();
    }
}
