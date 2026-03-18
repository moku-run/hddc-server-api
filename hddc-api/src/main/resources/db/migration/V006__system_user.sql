-- =============================================================
-- V006: 크롤러 전용 시스템 사용자
-- =============================================================

INSERT INTO mst_user (email, password, nickname, role)
VALUES ('system@hddc.dev', 'SYSTEM_NO_LOGIN', 'HDDC Bot', 'ADMIN')
ON CONFLICT (email) DO NOTHING;
