-- ============================================================
-- V5 – Event Store (Write Side)
-- ============================================================

-- Bảng gốc của Event Sourcing: append-only, không bao giờ UPDATE/DELETE.
-- Ràng buộc UNIQUE (aggregate_id, version) đóng vai trò Optimistic Lock,
-- thay thế hoàn toàn SELECT ... FOR UPDATE của V1.
CREATE TABLE domain_events (
    event_id        VARCHAR2(50)  NOT NULL,
    aggregate_id    VARCHAR2(50)  NOT NULL,
    aggregate_type  VARCHAR2(50)  NOT NULL,
    version         NUMBER(19)    NOT NULL,
    event_type      VARCHAR2(100) NOT NULL,
    payload         CLOB          NOT NULL,
    metadata        CLOB,
    occurred_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_domain_events     PRIMARY KEY (event_id),
    CONSTRAINT uq_aggregate_version UNIQUE (aggregate_id, version)
);

CREATE INDEX ix_domain_events_aggregate ON domain_events (aggregate_id, version ASC);

-- Snapshot: lưu trạng thái tổng hợp sau mỗi 50 sự kiện.
-- Khi load aggregate, chỉ cần replay từ version snapshot trở đi.
CREATE TABLE aggregate_snapshots (
    aggregate_id    VARCHAR2(50)  NOT NULL,
    aggregate_type  VARCHAR2(50)  NOT NULL,
    version         NUMBER(19)    NOT NULL,
    payload         CLOB          NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_aggregate_snapshots PRIMARY KEY (aggregate_id)
);