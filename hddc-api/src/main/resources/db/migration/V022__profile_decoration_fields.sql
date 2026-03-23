-- 프로필 꾸미기 기능 확장
ALTER TABLE mst_profile ADD COLUMN background_texture VARCHAR(20);
ALTER TABLE mst_profile ADD COLUMN decorator1_type VARCHAR(20);
ALTER TABLE mst_profile ADD COLUMN decorator1_text VARCHAR(80);
ALTER TABLE mst_profile ADD COLUMN decorator2_type VARCHAR(20);
ALTER TABLE mst_profile ADD COLUMN decorator2_text VARCHAR(80);
ALTER TABLE mst_profile ADD COLUMN link_gradient_from VARCHAR(20);
ALTER TABLE mst_profile ADD COLUMN link_gradient_to VARCHAR(20);
ALTER TABLE mst_profile ADD COLUMN link_border_color VARCHAR(20);
ALTER TABLE mst_profile ADD COLUMN link_border_thick VARCHAR(20) NOT NULL DEFAULT 'thin';
ALTER TABLE mst_profile ADD COLUMN page_layout VARCHAR(20) NOT NULL DEFAULT 'list';

-- 링크 상품 특화 필드
ALTER TABLE mst_profile_link ADD COLUMN price BIGINT;
ALTER TABLE mst_profile_link ADD COLUMN original_price BIGINT;
ALTER TABLE mst_profile_link ADD COLUMN discount_rate INTEGER;
ALTER TABLE mst_profile_link ADD COLUMN store VARCHAR(50);
ALTER TABLE mst_profile_link ADD COLUMN category VARCHAR(50);
ALTER TABLE mst_profile_link ADD COLUMN clicks BIGINT NOT NULL DEFAULT 0;
ALTER TABLE mst_profile_link ADD COLUMN likes BIGINT NOT NULL DEFAULT 0;
