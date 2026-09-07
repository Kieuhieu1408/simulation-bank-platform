package com.hieu.moneybank.shared.error;

/**
 * Phân loại lỗi theo SRD mục 13.3.
 *
 * <p>Phân loại này quyết định hành vi của caller, không chỉ để hiển thị:
 * chỉ {@link #TECHNICAL_RETRYABLE} được retry, và {@link #AMBIGUOUS} tuyệt đối
 * không được hiểu là thất bại (ADR-05).
 */
public enum ErrorCategory {

    /** Request sai schema/format. Client sửa dữ liệu rồi gửi lại được. */
    VALIDATION,

    /** Thiếu/sai/hết hạn credential. Không được tiết lộ lý do chi tiết. */
    AUTHENTICATION,

    /** Đã xác thực nhưng không đủ quyền hoặc không sở hữu resource. */
    AUTHORIZATION,

    /** Nghiệp vụ từ chối dứt điểm. Retry cùng dữ liệu luôn cho cùng kết quả. */
    BUSINESS_FINAL,

    /** Lỗi kỹ thuật xác định là chưa gây side effect. Được retry có backoff. */
    TECHNICAL_RETRYABLE,

    /**
     * Chưa xác định được kết quả ở hệ thống đích. Không được retry command,
     * chỉ được query theo reference (`AD-TR-R10`).
     */
    AMBIGUOUS
}
