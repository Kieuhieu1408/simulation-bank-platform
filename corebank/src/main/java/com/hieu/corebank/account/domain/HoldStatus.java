package com.hieu.corebank.account.domain;

/**
 * Trạng thái của một khoản tiền bị phong tỏa / giữ (Hold).
 */
public enum HoldStatus {
    /** Đang giữ (chờ hoàn tất hoặc giải tỏa) */
    HELD,
    /** Đã quyết toán thành công (tiền đã ra khỏi tài khoản) */
    SETTLED,
    /** Đã giải tỏa / hoàn trả lại cho số dư khả dụng */
    RELEASED
}
