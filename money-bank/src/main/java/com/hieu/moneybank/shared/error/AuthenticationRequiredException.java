package com.hieu.moneybank.shared.error;

/**
 * Không có định danh đã xác minh cho thao tác đang yêu cầu.
 *
 * <p>Dùng để fail closed ({@code AD-SEC-P05}): thiếu principal, thiếu claim bắt
 * buộc hoặc token không phải loại mong đợi đều dẫn tới từ chối, không có nhánh
 * nào cho phép chạy tiếp với định danh rỗng.
 */
public class AuthenticationRequiredException extends MoneyBankException {

    public AuthenticationRequiredException(String internalReason) {
        super(ErrorCode.UNAUTHENTICATED, internalReason);
    }
}
