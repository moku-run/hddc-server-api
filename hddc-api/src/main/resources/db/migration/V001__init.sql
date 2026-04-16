
CREATE TABLE users
(
    id                  BIGSERIAL    PRIMARY KEY,
    email               VARCHAR(255) NOT NULL UNIQUE,
    password            VARCHAR(255) NOT NULL,
    nickname            VARCHAR(50)  NOT NULL,
    role                VARCHAR(20)  NOT NULL DEFAULT 'USER',
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    is_locked           BOOLEAN      NOT NULL DEFAULT FALSE,
    login_attempt_count INT          NOT NULL DEFAULT 0,
    last_login_at       TIMESTAMPTZ,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          BIGINT       NOT NULL DEFAULT 0,
    updated_by          BIGINT       NOT NULL DEFAULT 0,
    deleted_at          TIMESTAMPTZ
);

COMMENT ON TABLE  users                     IS '회원 마스터';
COMMENT ON COLUMN users.role                IS '권한: USER / ADMIN';
COMMENT ON COLUMN users.login_attempt_count IS '로그인 실패 횟수 (잠금 임계값 초과 시 is_locked=true)';
COMMENT ON COLUMN users.last_login_at       IS '마지막 로그인 시각';
COMMENT ON COLUMN users.deleted_at          IS '탈퇴 처리 시각';

CREATE INDEX idx_users_email ON users (email);
CREATE UNIQUE INDEX uq_users_nickname ON users (nickname) WHERE is_deleted = FALSE;
