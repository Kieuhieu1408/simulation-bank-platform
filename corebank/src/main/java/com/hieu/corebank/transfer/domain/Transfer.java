package com.hieu.corebank.transfer.domain;

import com.hieu.corebank.shared.domain.*;
import com.hieu.corebank.transfer.domain.event.*;

import java.util.List;

/**
 * Transfer — Aggregate Root đại diện cho giao dịch Chuyển tiền (Event Sourcing).
 *
 * <p>State Machine:
 * {@code INITIATED -> SOURCE_RESERVED -> DESTINATION_CREDITED -> COMPLETED / FAILED}
 *
 * <p>Nguyên tắc thiết kế:
 * <ul>
 *   <li>100% thuần khiết Domain (không phụ thuộc Spring, JPA, Jackson).
 *   <li>Thẩm định Invariant Cấp 3: Nguồn != Đích; Số tiền > 0; Chuyển đổi trạng thái tuần tự.
 *   <li>Tất cả các bước đều mang tính Idempotent nội tại (gọi lại cùng bước -> no-op an toàn).
 * </ul>
 */
public class Transfer extends AggregateRoot<TransferId> {

    private TransferId id;
    private AccountId sourceAccountId;
    private AccountId destinationAccountId;
    private Money amount;
    private String description;
    private String requestHash;
    private TransferStatus status;
    private String failureReason;

    protected Transfer() {}

    // =========================================================================
    // Factory — Khởi tạo (sinh TransferInitiatedEvent)
    // =========================================================================

    public static Transfer initiate(TransferId id,
                                    AccountId sourceAccountId,
                                    AccountId destinationAccountId,
                                    Money amount,
                                    String description,
                                    String requestHash) {
        if (id == null) {
            throw new DomainException("TransferId cannot be null");
        }
        if (sourceAccountId == null || destinationAccountId == null) {
            throw new DomainException("Source and destination account IDs cannot be null");
        }
        if (sourceAccountId.equals(destinationAccountId)) {
            throw new DomainException("Source and destination accounts must be different");
        }
        if (amount == null) {
            throw new DomainException("Amount cannot be null");
        }
        amount.requirePositive();
        if (requestHash == null || requestHash.trim().isEmpty()) {
            throw new DomainException("Request hash cannot be null or blank");
        }

        Transfer transfer = new Transfer();
        transfer.applyNew(new TransferInitiatedEvent(
                id.value(),
                1L,
                sourceAccountId.value(),
                destinationAccountId.value(),
                amount.amount(),
                amount.currency().code(),
                description,
                requestHash
        ));
        return transfer;
    }

    // =========================================================================
    // Reconstitute — nạp từ lịch sử Event (Event Sourcing)
    // =========================================================================

    public static Transfer reconstitute(List<DomainEvent> history) {
        if (history == null || history.isEmpty()) {
            throw new IllegalArgumentException("History cannot be empty");
        }
        Transfer transfer = new Transfer();
        for (DomainEvent event : history) {
            transfer.apply(event);
            transfer.version = event.version();
        }
        return transfer;
    }

    // =========================================================================
    // State Transitions (Idempotent Domain Methods)
    // =========================================================================

    /**
     * Xác nhận đã giữ tiền trên tài khoản nguồn.
     */
    public void markSourceReserved() {
        if (this.status == TransferStatus.SOURCE_RESERVED) {
            // Idempotent no-op
            return;
        }
        if (this.status != TransferStatus.INITIATED) {
            throw new DomainException("Cannot mark source reserved from status: " + this.status);
        }
        applyNew(new TransferFundsReservedEvent(id.value(), nextVersion()));
    }

    /**
     * Xác nhận đã ghi có tài khoản đích (Điểm Pivot).
     */
    public void markDestinationCredited() {
        if (this.status == TransferStatus.DESTINATION_CREDITED) {
            // Idempotent no-op
            return;
        }
        if (this.status != TransferStatus.SOURCE_RESERVED) {
            throw new DomainException("Cannot mark destination credited from status: " + this.status);
        }
        applyNew(new TransferDestinationCreditedEvent(id.value(), nextVersion()));
    }

    /**
     * Hoàn tất giao dịch chuyển tiền (Settle thành công).
     */
    public void complete() {
        if (this.status == TransferStatus.COMPLETED) {
            // Idempotent no-op
            return;
        }
        if (this.status != TransferStatus.DESTINATION_CREDITED) {
            throw new DomainException("Cannot complete transfer from status: " + this.status);
        }
        applyNew(new TransferCompletedEvent(
                id.value(), nextVersion(),
                sourceAccountId != null ? sourceAccountId.value() : null,
                destinationAccountId != null ? destinationAccountId.value() : null,
                amount != null ? amount.amount() : null,
                amount != null ? amount.currency().code() : null
        ));
    }

    /**
     * Đánh dấu giao dịch thất bại.
     */
    public void fail(String reason) {
        if (this.status == TransferStatus.FAILED) {
            // Idempotent no-op
            return;
        }
        if (this.status == TransferStatus.COMPLETED) {
            throw new DomainException("Cannot fail an already completed transfer");
        }
        if (this.status == TransferStatus.DESTINATION_CREDITED) {
            throw new DomainException("Cannot fail transfer after destination has been credited (post-pivot invariant: settlement must proceed)");
        }
        applyNew(new TransferFailedEvent(
                id.value(), nextVersion(),
                sourceAccountId != null ? sourceAccountId.value() : null,
                destinationAccountId != null ? destinationAccountId.value() : null,
                amount != null ? amount.amount() : null,
                amount != null ? amount.currency().code() : null,
                reason
        ));
    }

    // =========================================================================
    // Event Application (State Mutators — chỉ cập nhật RAM)
    // =========================================================================

    @Override
    protected void apply(DomainEvent event) {
        switch (event) {
            case TransferInitiatedEvent e -> {
                this.id = TransferId.of(e.aggregateId());
                this.sourceAccountId = AccountId.of(e.sourceAccountId());
                this.destinationAccountId = AccountId.of(e.destinationAccountId());
                this.amount = Money.of(e.amount(), Currency.of(e.currency()));
                this.description = e.description();
                this.requestHash = e.requestHash();
                this.status = TransferStatus.INITIATED;
            }
            case TransferFundsReservedEvent e -> {
                this.status = TransferStatus.SOURCE_RESERVED;
            }
            case TransferDestinationCreditedEvent e -> {
                this.status = TransferStatus.DESTINATION_CREDITED;
            }
            case TransferCompletedEvent e -> {
                this.status = TransferStatus.COMPLETED;
            }
            case TransferFailedEvent e -> {
                this.status = TransferStatus.FAILED;
                this.failureReason = e.reason();
            }
            default -> throw new IllegalArgumentException("Unknown transfer event: " + event.getClass().getName());
        }
    }

    // =========================================================================
    // Getters
    // =========================================================================

    @Override
    public TransferId id() {
        return id;
    }

    public AccountId sourceAccountId() {
        return sourceAccountId;
    }

    public AccountId destinationAccountId() {
        return destinationAccountId;
    }

    public Money amount() {
        return amount;
    }

    public String description() {
        return description;
    }

    public String requestHash() {
        return requestHash;
    }

    public TransferStatus status() {
        return status;
    }

    public String failureReason() {
        return failureReason;
    }
}
