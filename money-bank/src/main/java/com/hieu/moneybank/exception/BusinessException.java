package com.hieu.moneybank.exception;

import com.hieu.common.constant.CommonErrorCode;
import com.hieu.common.exception.CommonException;

/**
 * Lỗi nghiệp vụ dứt điểm trong money-bank.
 *
 * <p>Extends {@link CommonException} để {@link com.hieu.common.exception.GlobalExceptionHandler}
 * xử lý thống nhất và trả về HTTP 422 với mã lỗi số chuẩn.
 */
public class BusinessException extends CommonException {

    public BusinessException(String internalMessage) {
        super(CommonErrorCode.BUSINESS_RULE_VIOLATION, internalMessage);
    }
}
