CREATE UNIQUE INDEX uq_mst_user_nickname
    ON mst_user (nickname) WHERE is_deleted = FALSE;
