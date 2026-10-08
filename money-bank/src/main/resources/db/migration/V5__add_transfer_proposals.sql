-- ============================================================
-- V5 – Transfer Proposals (U-06: Transfer Command & State Machine)
-- ============================================================
--
-- Bảng này lưu Transfer Proposal — aggregate root của luồng chuyển tiền 2 bước.
--
-- State Machine:
--   PENDING → VALIDATING → CONFIRMED (terminal)
--           → REJECTED (terminal)
--   PENDING → VALIDATING → FAILED (terminal)
--
-- Idempotency: uk_proposal_idempotency_key bảo đảm mỗi ý định chỉ tạo 1 proposal.
-- OCC: cột version (Oracle optimistic locking) chặn concurrent confirm.
-- ============================================================

CREATE TABLE transfer_proposals (
    -- Định danh
    id                      VARCHAR2(36)    NOT NULL,
    initiator_customer_id   VARCHAR2(64)    NOT NULL,   -- CIF từ identity context

    -- Nghiệp vụ
    source_account_id       VARCHAR2(36)    NOT NULL,
    destination_account_id  VARCHAR2(36)    NOT NULL,
    amount                  NUMBER(19, 2)   NOT NULL,
    currency                VARCHAR2(3)     NOT NULL,
    idempotency_key         VARCHAR2(64)    NOT NULL,
    description             VARCHAR2(255),

    -- Trạng thái state machine
    status                  VARCHAR2(20)    NOT NULL,   -- PENDING/VALIDATING/CONFIRMED/FAILED/REJECTED

    -- Kết quả từ Corebank (null cho đến khi CONFIRMED)
    corebank_transaction_id VARCHAR2(64),

    -- Lý do thất bại/từ chối (null khi PENDING/CONFIRMED)
    failure_reason          VARCHAR2(255),

    -- Audit
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Optimistic Concurrency Control
    version                 NUMBER(19, 0)   DEFAULT 0 NOT NULL,

    -- Constraints
    CONSTRAINT pk_transfer_proposals            PRIMARY KEY (id),
    CONSTRAINT uk_proposal_idempotency_key      UNIQUE (idempotency_key),
    CONSTRAINT chk_proposal_amount_positive     CHECK (amount > 0),
    CONSTRAINT chk_proposal_accounts_different  CHECK (source_account_id <> destination_account_id),
    CONSTRAINT chk_proposal_status              CHECK (status IN (
        'PENDING', 'VALIDATING', 'CONFIRMED', 'FAILED', 'REJECTED'
    ))
);

-- Index cho query theo account (source hoặc destination)
CREATE INDEX idx_proposal_source    ON transfer_proposals (source_account_id, created_at);
CREATE INDEX idx_proposal_dest      ON transfer_proposals (destination_account_id, created_at);

-- Index cho query theo status (ví dụ tìm PENDING để reconciliation)
CREATE INDEX idx_proposal_status    ON transfer_proposals (status, created_at);

-- Index cho query theo người khởi tạo
CREATE INDEX idx_proposal_initiator ON transfer_proposals (initiator_customer_id, created_at);
