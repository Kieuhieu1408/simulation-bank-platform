-- ============================================================
-- V1 – Initial schema
-- ============================================================

-- Customers
CREATE TABLE customers (
    cif_number  VARCHAR2(20)  NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_customers     PRIMARY KEY (cif_number)
);

-- Account number sequence (prefix "10" + 10 digits → 12-char account number)
CREATE SEQUENCE account_number_seq
    START WITH 1000000000
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Accounts
CREATE TABLE accounts (
    id             VARCHAR2(36)    NOT NULL,
    account_number VARCHAR2(20)    NOT NULL,
    cif_number     VARCHAR2(20)    NOT NULL,
    currency       VARCHAR2(3)     NOT NULL,
    balance        NUMBER(19, 2)   NOT NULL,
    status         VARCHAR2(20)    NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_accounts        PRIMARY KEY (id),
    CONSTRAINT uk_account_number  UNIQUE (account_number),
    CONSTRAINT fk_account_cif     FOREIGN KEY (cif_number) REFERENCES customers (cif_number)
);

-- Bank cards
CREATE TABLE bank_cards (
    id          VARCHAR2(36)  NOT NULL,
    card_number VARCHAR2(16)  NOT NULL,
    cvv         VARCHAR2(3)   NOT NULL,
    account_id  VARCHAR2(36)  NOT NULL,
    status      VARCHAR2(20)  NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_bank_cards    PRIMARY KEY (id),
    CONSTRAINT uk_card_number   UNIQUE (card_number),
    CONSTRAINT fk_card_account  FOREIGN KEY (account_id) REFERENCES accounts (id)
);

-- Bank transactions
CREATE TABLE bank_transactions (
    id                     VARCHAR2(36)   NOT NULL,
    source_account_id      VARCHAR2(36)   NOT NULL,
    destination_account_id VARCHAR2(36)   NOT NULL,
    amount                 NUMBER(19, 2)  NOT NULL,
    currency               VARCHAR2(3)    NOT NULL,
    idempotency_key        VARCHAR2(100)  NOT NULL,
    description            VARCHAR2(255),
    status                 VARCHAR2(20)   NOT NULL,
    created_at             TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_bank_transactions   PRIMARY KEY (id),
    CONSTRAINT uk_idempotency_key     UNIQUE (idempotency_key),
    CONSTRAINT fk_tx_source           FOREIGN KEY (source_account_id)      REFERENCES accounts (id),
    CONSTRAINT fk_tx_destination      FOREIGN KEY (destination_account_id) REFERENCES accounts (id)
);

CREATE INDEX idx_tx_source      ON bank_transactions (source_account_id, created_at);
CREATE INDEX idx_tx_destination ON bank_transactions (destination_account_id, created_at);
