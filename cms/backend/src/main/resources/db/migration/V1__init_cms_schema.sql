CREATE TABLE cms_role (
    id UUID PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP
);

CREATE TABLE cms_permission (
    id UUID PRIMARY KEY,
    menu_code VARCHAR(100) NOT NULL,
    action_code VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    CONSTRAINT uk_menu_action UNIQUE (menu_code, action_code)
);

CREATE TABLE cms_role_permission (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    is_granted BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES cms_role(id),
    CONSTRAINT fk_permission FOREIGN KEY (permission_id) REFERENCES cms_permission(id)
);

CREATE TABLE proposal (
    id UUID PRIMARY KEY,
    proposal_code VARCHAR(50) NOT NULL UNIQUE,
    customer_cif VARCHAR(50) NOT NULL,
    proposal_type VARCHAR(50) NOT NULL,
    old_data TEXT,
    new_data TEXT,
    document_urls TEXT,
    status VARCHAR(50) NOT NULL,
    maker_id VARCHAR(100) NOT NULL,
    checker_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE proposal_change_log (
    id UUID PRIMARY KEY,
    proposal_id UUID,
    proposal_code VARCHAR(50) NOT NULL,
    customer_cif VARCHAR(50) NOT NULL,
    proposal_type VARCHAR(50) NOT NULL,
    old_data TEXT,
    new_data TEXT,
    document_urls TEXT,
    final_status VARCHAR(50) NOT NULL,
    maker_id VARCHAR(100) NOT NULL,
    checker_id VARCHAR(100),
    reason TEXT,
    completed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
