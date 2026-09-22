package com.hieu.common.idempotency;

/**
 * Trạng thái của một lần claim idempotency.
 *
 * <p>Phân biệt {@link #IN_PROGRESS} với trạng thái cuối là điều kiện để trả
 * {@code 202 PROCESSING} thay vì lặp lại command, theo SRD mục 6.2 lớp 2.
 */
public enum IdempotencyStatus {

    /** Đã claim nhưng chưa có kết quả cuối. Request trùng nhận {@code 202}. */
    IN_PROGRESS,

    /** Đã có kết quả cuối thành công. Request trùng nhận lại response đã lưu. */
    COMPLETED,

    /**
     * Đã có kết quả cuối là thất bại nghiệp vụ. Vẫn là trạng thái cuối: request
     * trùng nhận lại đúng lỗi cũ, không được thử lại như request mới.
     */
    FAILED;

    public boolean isTerminal() {
        return this != IN_PROGRESS;
    }
}
