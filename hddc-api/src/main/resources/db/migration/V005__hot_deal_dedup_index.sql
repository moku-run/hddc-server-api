
CREATE UNIQUE INDEX uq_hot_deal_url
    ON hot_deal (url) WHERE is_deleted = FALSE;

CREATE INDEX idx_hot_deal_recent_title
    ON hot_deal (created_at DESC, title) WHERE is_deleted = FALSE;
