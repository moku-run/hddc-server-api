
INSERT INTO users (email, password, nickname, role)
VALUES ('system@hddc.dev', 'SYSTEM_NO_LOGIN', 'HDDC Bot', 'ADMIN')
ON CONFLICT (email) DO NOTHING;
