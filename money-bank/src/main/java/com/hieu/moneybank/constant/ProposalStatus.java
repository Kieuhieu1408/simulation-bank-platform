package com.hieu.moneybank.constant;

/**
 * Trạng thái của TransferProposal theo state machine U-06.
 *
 * <pre>
 *          createProposal()
 * [START] ──────────────────► PENDING
 *                                │
 *                    confirm()   │   reject()
 *               ┌───────────────┤──────────────────┐
 *               ▼               │                  ▼
 *         VALIDATING       (invalid input)      REJECTED (terminal)
 *               │
 *     ┌─────────┴──────────┐
 *     │ corebank ok        │ corebank fail / invariant fail
 *     ▼                    ▼
 *  CONFIRMED            FAILED
 * (terminal)          (terminal)
 * </pre>
 *
 * <p>Chỉ PENDING mới cho phép confirm(). VALIDATING là trạng thái ngắn trong cùng
 * transaction — được giữ để detect concurrent confirm cùng proposal (OCC).
 */
public enum ProposalStatus {

    /** Proposal vừa được tạo, chờ người dùng hoặc hệ thống xác nhận. */
    PENDING,

    /**
     * Đang validate và gọi Corebank (trong transaction confirm).
     * Trạng thái này chống race condition khi hai request cùng confirm.
     */
    VALIDATING,

    /** Corebank đã ghi sổ thành công — trạng thái cuối. */
    CONFIRMED,

    /** Corebank từ chối hoặc lỗi nghiệp vụ — trạng thái cuối. */
    FAILED,

    /** Proposal bị từ chối trước khi gọi Corebank — trạng thái cuối. */
    REJECTED;

    public boolean isTerminal() {
        return this == CONFIRMED || this == FAILED || this == REJECTED;
    }

    public boolean canConfirm() {
        return this == PENDING;
    }

    public boolean canReject() {
        return this == PENDING;
    }
}
