package com.hieu.corebank.transfer.application.port;

import com.hieu.corebank.shared.domain.TransferId;

/**
 * Outbound Port quản lý In-Flight Distributed Lock cho Idempotency của luồng Chuyển tiền.
 *
 * <p>Thiết kế theo Cách 1 (Zero Long-Term RAM Overhead):
 * <ul>
 *   <li>Chỉ giữ khóa trên Redis trong lúc giao dịch đang xử lý ({@code IN_FLIGHT}).</li>
 *   <li>Chủ động xóa khóa ngay lập tức khi luồng thực thi Saga hoàn tất.</li>
 *   <li>Tự động fallback an toàn về Database Constraint nếu Redis tạm thời không khả dụng.</li>
 * </ul>
 */
public interface TransferIdempotencyLockPort {

    sealed interface LockResult permits LockResult.Acquired, LockResult.AlreadyInFlight, LockResult.LockUnavailable {
        /** Lấy lock thành công; caller có trách nhiệm gọi {@link LockHandle#release()} khi xử lý xong. */
        record Acquired(LockHandle handle) implements LockResult {}

        /** Đang có một Pod/luồng khác giữ lock xử lý cùng {@code transferId}. */
        record AlreadyInFlight(String existingRequestHash) implements LockResult {}

        /** Redis tạm thời không khả dụng -> fallback về chốt chặn Database. */
        record LockUnavailable() implements LockResult {}
    }

    @FunctionalInterface
    interface LockHandle {
        void release();
    }

    LockResult tryAcquire(TransferId transferId, String requestHash);
}
