-- comment_count를 실제 비삭제 댓글 수에 맞춤
UPDATE mst_hot_deal d
SET comment_count = (
    SELECT COUNT(*)
    FROM mst_hot_deal_comment c
    WHERE c.deal_id = d.id AND c.is_deleted = false
);
