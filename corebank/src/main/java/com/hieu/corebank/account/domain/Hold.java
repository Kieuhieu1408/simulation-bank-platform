package com.hieu.corebank.account.domain;

import com.hieu.corebank.shared.domain.Money;
import com.hieu.corebank.shared.domain.TransferId;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Entity đại diện cho một khoản phong tỏa / giữ tiền trên tài khoản thanh toán.
 */
public final class Hold implements Serializable {

    private final TransferId transferId;
    private final Money      amount;
    private final HoldStatus status;
    private final Instant    createdAt;

    public Hold(TransferId transferId, Money amount, HoldStatus status, Instant createdAt) {
        this.transferId = Objects.requireNonNull(transferId, "transferId cannot be null");
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
    }

    public TransferId transferId() {
        return transferId;
    }

    public Money amount() {
        return amount;
    }

    public HoldStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public boolean isHeld() {
        return status == HoldStatus.HELD;
    }

    public boolean isSettled() {
        return status == HoldStatus.SETTLED;
    }

    public boolean isReleased() {
        return status == HoldStatus.RELEASED;
    }

    public Hold settle() {
        return new Hold(transferId, amount, HoldStatus.SETTLED, createdAt);
    }

    public Hold release() {
        return new Hold(transferId, amount, HoldStatus.RELEASED, createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hold hold = (Hold) o;
        return Objects.equals(transferId, hold.transferId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transferId);
    }

    @Override
    public String toString() {
        return "Hold{" +
                "transferId=" + transferId +
                ", amount=" + amount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
