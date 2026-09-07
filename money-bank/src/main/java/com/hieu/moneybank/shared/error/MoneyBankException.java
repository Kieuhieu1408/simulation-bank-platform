package com.hieu.moneybank.shared.error;

/**
 * Exception gốc của Money Bank, luôn mang theo một {@link ErrorCode} đã được
 * ánh xạ sẵn sang HTTP status và {@link ErrorCategory}.
 *
 * <p>Mục đích: mọi lỗi đi ra ngoài đều có mã trong error catalog, không có
 * đường nào để một exception kỹ thuật lọt nguyên trạng tới client.
 */
public class MoneyBankException extends RuntimeException {

    private final ErrorCode errorCode;

    protected MoneyBankException(ErrorCode errorCode, String internalMessage) {
        super(internalMessage);
        this.errorCode = errorCode;
    }

    protected MoneyBankException(ErrorCode errorCode, String internalMessage, Throwable cause) {
        super(internalMessage, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode errorCode() {
        return errorCode;
    }

    /**
     * Thông báo an toàn để trả client. Tách khỏi {@link #getMessage()} vì
     * {@code getMessage()} phục vụ log nội bộ và có thể chứa chi tiết kỹ thuật.
     */
    public String safeMessage() {
        return errorCode.safeMessage();
    }
}
