package com.hieu.moneybank.shared.error;

/**
 * Request sai định dạng ở mức không thể xử lý tiếp, phát hiện ngoài phạm vi bean
 * validation (ví dụ idempotency key không đúng khuôn dạng cho phép).
 */
public class InvalidRequestException extends MoneyBankException {

    public InvalidRequestException(String internalReason) {
        super(ErrorCode.REQUEST_INVALID, internalReason);
    }
}
