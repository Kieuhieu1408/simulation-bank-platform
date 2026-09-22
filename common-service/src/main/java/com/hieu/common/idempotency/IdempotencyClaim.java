package com.hieu.common.idempotency;

/**
 * Kết quả của một lần claim idempotency.
 *
 * <p>Sealed interface để compiler bắt buộc caller xử lý đủ ba nhánh. Nếu dùng
 * boolean hoặc enum, rất dễ bỏ sót nhánh {@link InProgress} và xử lý nó như
 * request mới — đúng lỗi tạo giao dịch trùng.
 *
 * <p>Trường hợp cùng key khác hash không có ở đây: đó là lỗi dứt điểm và được
 * biểu diễn bằng {@link com.hieu.common.exception.IdempotencyConflictException}.
 */
public sealed interface IdempotencyClaim {

    IdempotencyRecord record();

    /** Request này thắng claim và là request duy nhất được phép thực thi command. */
    record Claimed(IdempotencyRecord record) implements IdempotencyClaim {
    }

    /**
     * Một request cùng ý định đang được xử lý. Caller phải trả
     * {@code 202 PROCESSING} kèm {@code transactionId}, không được gọi lại
     * downstream.
     */
    record InProgress(IdempotencyRecord record) implements IdempotencyClaim {
    }

    /**
     * Ý định này đã có kết quả cuối. Caller phải trả lại kết quả đã lưu, kể cả
     * khi kết quả đó là thất bại nghiệp vụ.
     */
    record AlreadySettled(IdempotencyRecord record) implements IdempotencyClaim {
    }
}
