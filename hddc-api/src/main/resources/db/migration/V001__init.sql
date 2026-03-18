CREATE TABLE mst_user
(
    id                  BIGSERIAL PRIMARY KEY,
    email               VARCHAR(255) NOT NULL UNIQUE,
    password            VARCHAR(255) NOT NULL,
    nickname            VARCHAR(50)  NOT NULL,
    role                VARCHAR(20)  NOT NULL DEFAULT 'USER',
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    is_locked           BOOLEAN      NOT NULL DEFAULT FALSE,
    login_attempt_count INT          NOT NULL DEFAULT 0,
    last_login_at       TIMESTAMP,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_mst_user_email ON mst_user (email);
