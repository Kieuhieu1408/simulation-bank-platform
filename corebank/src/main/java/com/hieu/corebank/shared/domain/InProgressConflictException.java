package com.hieu.corebank.shared.domain;

/**
 * Ném ra khi một yêu cầu mang cùng {@code idempotencyKey} đang được xử lý đồng thời (in-flight)
 * trên một Pod/luồng khác. Ánh xạ sang HTTP 409 Conflict với mã lỗi riêng (409002) để client
 * phân biệt với lỗi xung đột sai lệch payload (409001).
 */
public class InProgressConflictException extends ConflictException {
    public InProgressConflictException(String message) {
        super(message);
    }
}
