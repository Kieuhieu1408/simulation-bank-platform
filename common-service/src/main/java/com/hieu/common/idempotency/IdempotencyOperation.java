package com.hieu.common.idempotency;

/**
 * Loại command được bảo vệ bằng idempotency.
 *
 * <p>Operation là một phần của unique key {@code (customer_id, operation,
 * idempotency_key)}. Tách theo operation để một key vô tình trùng giữa hai loại
 * nghiệp vụ khác nhau không chặn lẫn nhau, đồng thời giữ đúng phạm vi "một ý
 * định của một khách hàng cho một operation" (SRD mục 4).
 */
public enum IdempotencyOperation {

    /** {@code POST /api/v1/transfers}. */
    TRANSFER_CREATE,

    /** {@code POST /api/v1/account-proposals}. Key được truyền tiếp sang Profile Service. */
    ACCOUNT_PROPOSAL_CREATE
}
