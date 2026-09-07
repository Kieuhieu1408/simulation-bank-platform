package com.hieu.moneybank.shared.error;

/**
 * Cùng idempotency key nhưng canonical request hash khác.
 *
 * <p>Theo BRD 3.2.4 và {@code AD-DC-C01}, đây là lỗi dứt điểm: không được xử lý
 * request, không được tạo giao dịch mới và không được ghi đè hash cũ.
 *
 * <p>Message nội bộ cố tình không chứa hash để tránh rò dữ liệu request qua log.
 */
public class IdempotencyConflictException extends MoneyBankException {

    private final String operation;

    public IdempotencyConflictException(String operation) {
        super(ErrorCode.IDEMPOTENCY_CONFLICT,
                "Idempotency key đã tồn tại với request hash khác cho operation " + operation);
        this.operation = operation;
    }

    public String operation() {
        return operation;
    }
}
