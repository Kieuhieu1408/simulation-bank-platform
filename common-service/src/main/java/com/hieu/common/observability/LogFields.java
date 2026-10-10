package com.hieu.common.observability;

/**
 * Tên field bắt buộc của structured log theo SRD mục 10.4.
 *
 * <p>Dùng hằng số thay vì string literal để tên field không bị lệch giữa các
 * module, vì dashboard và alert phụ thuộc trực tiếp vào tên này.
 *
 * <p>Identifier không liên quan tới ngữ cảnh thì để trống, không tạo giá trị giả.
 */
public final class LogFields {

    public static final String CORRELATION_ID = "correlationId";
    public static final String WORKFLOW_ID = "workflowId";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String PROPOSAL_ID = "proposalId";
    public static final String EVENT_NAME = "eventName";
    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_CATEGORY = "errorCategory";
    public static final String DURATION_MS = "durationMs";

    /** Header nhận correlation id từ trusted edge (SRD mục 13.1). */
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    // --- Log Event Names ---
    public static final String EVENT_REQUEST_REJECTED = "REQUEST_REJECTED";
    public static final String EVENT_REQUEST_VALIDATION_FAILED = "REQUEST_VALIDATION_FAILED";
    public static final String EVENT_AUTHORIZATION_DENIED = "AUTHORIZATION_DENIED";
    public static final String EVENT_AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
    public static final String EVENT_UNHANDLED_ERROR = "UNHANDLED_ERROR";

    private LogFields() {
    }
}
