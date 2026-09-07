-- Schema tham chiếu cho bảng idempotency_record (Oracle syntax).
--
-- File này KHÔNG được tự động áp dụng: công cụ migration (Flyway/Liquibase) và
-- database đích chưa được chốt (OQ-P1-12, AD-DC-OPEN-03). Đây là bản đặc tả để
-- review cùng DBA và là nguồn cho migration thật khi có quyết định.
--
-- Điểm bắt buộc giữ nguyên khi hiện thực hóa:
--   1. uk_idem_customer_operation_key là cơ chế chống trùng cuối cùng. Không
--      được thay bằng index thường hay bằng kiểm tra ở tầng ứng dụng.
--   2. request_hash không được UPDATE sau khi tạo (AD-DC-C06).
--   3. Không cascade delete; purge/archive theo retention đã phê duyệt
--      (AD-DC-D05, AD-DC-D06).

CREATE TABLE idempotency_record (
    id                NUMBER(19)     GENERATED ALWAYS AS IDENTITY,
    customer_id       VARCHAR2(64)   NOT NULL,
    operation         VARCHAR2(64)   NOT NULL,
    idempotency_key   VARCHAR2(64)   NOT NULL,
    request_hash      CHAR(64)       NOT NULL,
    transaction_id    VARCHAR2(64),
    workflow_id       VARCHAR2(64),
    status            VARCHAR2(32)   NOT NULL,
    response_code     VARCHAR2(32),
    response_snapshot VARCHAR2(4000),
    created_at        TIMESTAMP(6)   NOT NULL,
    updated_at        TIMESTAMP(6)   NOT NULL,
    expires_at        TIMESTAMP(6),
    version           NUMBER(19)     DEFAULT 0 NOT NULL,
    CONSTRAINT pk_idempotency_record PRIMARY KEY (id),
    CONSTRAINT uk_idem_customer_operation_key UNIQUE (customer_id, operation, idempotency_key),
    CONSTRAINT uk_idem_transaction_id UNIQUE (transaction_id),
    CONSTRAINT ck_idem_status CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'FAILED'))
);

CREATE INDEX ix_idem_workflow_id ON idempotency_record (workflow_id);
CREATE INDEX ix_idem_expires_at ON idempotency_record (expires_at);
