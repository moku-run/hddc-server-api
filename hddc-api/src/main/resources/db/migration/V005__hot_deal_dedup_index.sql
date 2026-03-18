-- =============================================================
-- V005: 핫딜 중복 방지 인덱스 (크롤러 지원)
-- =============================================================

-- URL 중복 방지 (soft delete 된 건 제외)
CREATE UNIQUE INDEX uq_mst_hot_deal_url
    ON mst_hot_deal (url) WHERE is_deleted = FALSE;

-- 최근 제목 조회 (유사도 체크용)
CREATE INDEX idx_mst_hot_deal_recent_title
    ON mst_hot_deal (created_at DESC, title) WHERE is_deleted = FALSE;

-- 크롤링 소스 사이트 필터링 (remark1에 소스 사이트명 저장)
CREATE INDEX idx_mst_hot_deal_source
    ON mst_hot_deal (remark1) WHERE remark1 IS NOT NULL;
