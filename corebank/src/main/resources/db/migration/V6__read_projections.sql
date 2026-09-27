-- ============================================================
-- V6 – Read Side Projections
-- ============================================================

-- account_view: nguồn sự thật cho chiều Query (Mobile App, Dashboard).
-- Không bao giờ dùng bảng này để validate Command.
-- Cột last_event_version là chìa khóa chống xử lý event trùng lặp (Idempotency).
CREATE TABLE account_view (
    account_id            VARCHAR2(50)  NOT NULL,
    account_number        VARCHAR2(20)  NOT NULL,
    customer_id           VARCHAR2(20)  NOT NULL,
    currency              VARCHAR2(3)   NOT NULL,
    actual_balance        NUMBER(19, 4) DEFAULT 0 NOT NULL,
    available_balance     NUMBER(19, 4) DEFAULT 0 NOT NULL,
    status                VARCHAR2(20)  NOT NULL,
    last_event_version    NUMBER(19)    DEFAULT 0 NOT NULL,
    last_updated_at       TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_account_view PRIMARY KEY (account_id)
);

CREATE INDEX ix_account_view_customer ON account_view (customer_id);

-- transaction_history_view: sao kê hiển thị cho User (Statement).
-- balance_after cho phép User thấy số dư sau mỗi giao dịch — đặc trưng ngân hàng.
CREATE TABLE transaction_history_view (
    id            VARCHAR2(50)  NOT NULL,
    account_id    VARCHAR2(50)  NOT NULL,
    direction     VARCHAR2(6)   NOT NULL,
    amount        NUMBER(19, 4) NOT NULL,
    currency      VARCHAR2(3)   NOT NULL,
    balance_after NUMBER(19, 4) NOT NULL,
    event_type    VARCHAR2(100) NOT NULL,
    description   VARCHAR2(255),
    occurred_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_txhv PRIMARY KEY (id)
);

CREATE INDEX ix_txhv_account_date ON transaction_history_view (account_id, occurred_at DESC);