package com.hieu.moneybank.exception;

import com.hieu.common.constant.CommonErrorCode;
import com.hieu.common.exception.CommonException;

/**
 * Không tìm thấy tài nguyên yêu cầu trong money-bank.
 *
 * <p>Extends {@link CommonException} để {@link com.hieu.common.exception.GlobalExceptionHandler}
 * xử lý thống nhất và trả về HTTP 404 với mã lỗi số chuẩn.
 */
public class NotFoundException extends CommonException {

    public NotFoundException(String internalMessage) {
        super(CommonErrorCode.RESOURCE_NOT_FOUND, internalMessage);
    }
}
