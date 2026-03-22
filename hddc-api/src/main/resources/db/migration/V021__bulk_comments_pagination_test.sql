-- deal_id=54 커서 페이지네이션 테스트용 벌크 데이터
-- 루트 댓글 60건 + 대댓글 + 삭제 댓글 포함

INSERT INTO mst_hot_deal_comment (deal_id, user_id, parent_id, content, created_at, updated_at, created_by, updated_by, is_deleted) VALUES
-- 루트 댓글 60건
(54, 1, NULL, '와우 멤버십 가입 3개월째인데 진짜 만족합니다', NOW() - INTERVAL '10 hours', NOW(), 1, 1, false),
(54, 1, NULL, '쿠팡 없으면 살 수 없는 몸이 됐어요', NOW() - INTERVAL '9 hours 55 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '로켓배송 속도 진짜 미쳤음 새벽 3시에 시켜도 아침에 옴', NOW() - INTERVAL '9 hours 50 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '이번 달 배송비 절약한 금액만 3만원 넘음', NOW() - INTERVAL '9 hours 45 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '쿠팡플레이 예능 라인업이 좋아졌더라', NOW() - INTERVAL '9 hours 40 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '와우 전용 할인 쿠폰 자주 뿌리더라구요', NOW() - INTERVAL '9 hours 35 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '식료품은 거의 마트 안 가고 쿠팡으로 해결', NOW() - INTERVAL '9 hours 30 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '무거운 물 생수 같은거 집앞까지 와서 너무 편함', NOW() - INTERVAL '9 hours 25 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '반품이 쉬워서 옷도 쿠팡에서 시켜봄', NOW() - INTERVAL '9 hours 20 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '와우 가격 인상 소문 있던데 지금 가입하는게 나을듯', NOW() - INTERVAL '9 hours 15 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '로켓프레시 과일 퀄리티가 마트급임', NOW() - INTERVAL '9 hours 10 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '새벽배송 문앞에 놓고 가는데 도난 걱정은 없나요?', NOW() - INTERVAL '9 hours 5 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '쿠팡이 상장하고 나서 서비스 질이 더 좋아진 것 같음', NOW() - INTERVAL '9 hours', NOW(), 1, 1, false),
(54, 1, NULL, '자취생 필수템 인정합니다', NOW() - INTERVAL '8 hours 55 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '배송 기사님들 항상 감사합니다', NOW() - INTERVAL '8 hours 50 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '쿠팡플레이 축구 중계도 해주면 좋겠다', NOW() - INTERVAL '8 hours 45 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '리뷰 작성하면 캐시백 주는 것도 꿀이에요', NOW() - INTERVAL '8 hours 40 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '부모님 댁에도 로켓와우 가입시켜드림', NOW() - INTERVAL '8 hours 35 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '타 쇼핑몰 대비 가격 경쟁력도 있는 편', NOW() - INTERVAL '8 hours 30 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '교환 반품 프로세스가 진짜 빠르고 편함', NOW() - INTERVAL '8 hours 25 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '와우 멤버십 연간 결제 옵션도 있으면 좋겠다', NOW() - INTERVAL '8 hours 20 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '쿠팡 파트너스로 수익도 나오더라', NOW() - INTERVAL '8 hours 15 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '로켓배송 아닌 상품은 배송 느린 경우도 있음 주의', NOW() - INTERVAL '8 hours 10 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '캠핑용품도 쿠팡에서 다 구매 가능', NOW() - INTERVAL '8 hours 5 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '쿠팡 검색 알고리즘이 점점 좋아지는 듯', NOW() - INTERVAL '8 hours', NOW(), 1, 1, false),
(54, 1, NULL, '해외직구 상품도 로켓배송으로 빨리 와서 좋음', NOW() - INTERVAL '7 hours 55 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '생필품 정기배송 설정해두면 편해요', NOW() - INTERVAL '7 hours 50 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '쿠팡이츠도 와우 혜택 연동 좀 해주라', NOW() - INTERVAL '7 hours 45 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '리퍼 상품도 가성비 좋더라구요', NOW() - INTERVAL '7 hours 40 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '쿠팡 고객센터 응대가 좀 아쉬운 편이긴 함', NOW() - INTERVAL '7 hours 35 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '가전제품 설치 서비스도 무료인 거 아셨나요?', NOW() - INTERVAL '7 hours 30 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '추석 선물세트도 쿠팡이 젤 쌌음', NOW() - INTERVAL '7 hours 25 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '와우 회원 전용 특가 상품 알림 설정 추천', NOW() - INTERVAL '7 hours 20 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '쿠팡 앱 위젯으로 배송 추적하는 거 편함', NOW() - INTERVAL '7 hours 15 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '냉동식품 퀄리티가 생각보다 좋아서 놀람', NOW() - INTERVAL '7 hours 10 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '쿠팡플레이 키즈 콘텐츠도 있어서 애기 있는 집 좋음', NOW() - INTERVAL '7 hours 5 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '로켓직구 미국 상품 가격이 아마존보다 쌀 때도 있음', NOW() - INTERVAL '7 hours', NOW(), 1, 1, false),
(54, 1, NULL, '포장이 과대포장인 건 좀 아쉬움 환경 생각하면', NOW() - INTERVAL '6 hours 55 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '쿠팡 멤버십 가격이 네이버보다 싼 건 맞는데 혜택 비교가 필요함', NOW() - INTERVAL '6 hours 50 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '새벽배송 시간 지정 가능한 거 아시죠?', NOW() - INTERVAL '6 hours 45 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '펫 용품도 로켓배송으로 빠르게 받을 수 있어서 좋음', NOW() - INTERVAL '6 hours 40 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '쿠팡 라이브 방송 할인도 꽤 괜찮아요', NOW() - INTERVAL '6 hours 35 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '와우 멤버십 공유 기능은 없나요?', NOW() - INTERVAL '6 hours 30 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '골드박스 타임딜 매일 체크하는 재미가 있음', NOW() - INTERVAL '6 hours 25 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '결제 수단에 따라 추가 할인도 받을 수 있어요', NOW() - INTERVAL '6 hours 20 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '유아용품은 거의 쿠팡으로만 사는 중', NOW() - INTERVAL '6 hours 15 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '재구매율 높은 상품 추천 기능 좋더라', NOW() - INTERVAL '6 hours 10 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '명절 때 택배 대란에도 로켓배송은 정상이었음', NOW() - INTERVAL '6 hours 5 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '쿠팡 브랜드 PB상품 가성비 좋은 거 많아요', NOW() - INTERVAL '6 hours', NOW(), 1, 1, false),
(54, 1, NULL, '로켓와우 한 달만 써봐도 해지 못 함 ㅋㅋ', NOW() - INTERVAL '5 hours 55 minutes', NOW(), 1, 1, false),
-- 삭제된 댓글 (대댓글 없음 → 목록에서 제외되어야 함)
(54, 1, NULL, '이건 삭제될 댓글입니다 1', NOW() - INTERVAL '5 hours 50 minutes', NOW(), 1, 1, true),
(54, 1, NULL, '이건 삭제될 댓글입니다 2', NOW() - INTERVAL '5 hours 45 minutes', NOW(), 1, 1, true),
(54, 1, NULL, '이건 삭제될 댓글입니다 3', NOW() - INTERVAL '5 hours 40 minutes', NOW(), 1, 1, true),
-- 삭제된 댓글 (대댓글 있음 → "[삭제된 메시지입니다.]"로 표시되어야 함)
(54, 1, NULL, '이건 삭제되었지만 대댓글이 있는 댓글', NOW() - INTERVAL '5 hours 35 minutes', NOW(), 1, 1, true),
-- 추가 루트 댓글
(54, 1, NULL, '쿠팡 와우 멤버십으로 생활비 진짜 많이 아꼈어요', NOW() - INTERVAL '5 hours 30 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '주변 친구들한테도 추천해서 3명이나 가입함', NOW() - INTERVAL '5 hours 25 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '와우 멤버십 최고의 구독 서비스라고 생각합니다', NOW() - INTERVAL '5 hours 20 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '쿠팡 없이 어떻게 살았는지 모르겠다', NOW() - INTERVAL '5 hours 15 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '첫 달 무료니까 일단 써보세요 후회 없을 겁니다', NOW() - INTERVAL '5 hours 10 minutes', NOW(), 1, 1, false),
(54, 1, NULL, '이 딜 보고 바로 엄마한테 카톡 보냄ㅋㅋ', NOW() - INTERVAL '5 hours 5 minutes', NOW(), 1, 1, false);

-- 위에서 추가된 루트 댓글들에 대한 대댓글
-- ※ parent_id는 자동 증가 ID에 따라 결정됨
-- 이전 마이그레이션까지 약 310개 댓글 존재 → 새 댓글 ID는 311부터 시작
-- 311번 댓글 (와우 멤버십 3개월째) 대댓글
INSERT INTO mst_hot_deal_comment (deal_id, user_id, parent_id, content, created_at, updated_at, created_by, updated_by, is_deleted) VALUES
(54, 1, 311, '저도 3개월 됐는데 해지할 생각이 없어요', NOW() - INTERVAL '9 hours 58 minutes', NOW(), 1, 1, false),
(54, 1, 311, '한번 쓰면 빠져나올 수 없음ㅋㅋ', NOW() - INTERVAL '9 hours 56 minutes', NOW(), 1, 1, false),
(54, 1, 311, '6개월째인데 역대 최고의 구독', NOW() - INTERVAL '9 hours 54 minutes', NOW(), 1, 1, false),
-- 313번 댓글 (로켓배송 속도) 대댓글
(54, 1, 313, '새벽 3시는 좀 과장 아닌가요ㅋㅋ', NOW() - INTERVAL '9 hours 48 minutes', NOW(), 1, 1, false),
(54, 1, 313, '아닌데 진짜 새벽에 시켜도 다음날 아침에 옴', NOW() - INTERVAL '9 hours 46 minutes', NOW(), 1, 1, false),
-- 322번 댓글 (새벽배송 도난 걱정) 대댓글
(54, 1, 322, '아파트면 경비실에 맡겨주시더라구요', NOW() - INTERVAL '9 hours 3 minutes', NOW(), 1, 1, false),
(54, 1, 322, '문앞 사진 찍어서 알림 오니까 괜찮아요', NOW() - INTERVAL '9 hours 1 minutes', NOW(), 1, 1, false),
(54, 1, 322, '저는 안심번호로 연락 와서 직접 받았어요', NOW() - INTERVAL '8 hours 59 minutes', NOW(), 1, 1, false),
-- 330번 댓글 (부모님 댁) 대댓글
(54, 1, 328, '부모님이 매일 장 보러 안 나가셔도 돼서 좋아하심', NOW() - INTERVAL '8 hours 33 minutes', NOW(), 1, 1, false),
(54, 1, 328, '효도 인정ㅋㅋㅋ', NOW() - INTERVAL '8 hours 31 minutes', NOW(), 1, 1, false),
-- 341번 댓글 (와우 회원 전용 특가) 대댓글
(54, 1, 343, '알림 설정 어디서 하나요?', NOW() - INTERVAL '7 hours 18 minutes', NOW(), 1, 1, false),
(54, 1, 343, '쿠팡 앱 > 마이쿠팡 > 알림 설정에서 가능해요', NOW() - INTERVAL '7 hours 16 minutes', NOW(), 1, 1, false),
-- 364번 댓글 (삭제된 댓글이지만 대댓글 있음) 대댓글
(54, 1, 364, '삭제된 댓글이지만 이 대댓글은 보여야 합니다', NOW() - INTERVAL '5 hours 33 minutes', NOW(), 1, 1, false),
(54, 1, 364, '맞아요 원래 댓글은 삭제됐지만 대댓글은 남아있죠', NOW() - INTERVAL '5 hours 31 minutes', NOW(), 1, 1, false),
-- 350번 댓글 (로켓와우 한 달만 써봐도) 대댓글
(54, 1, 360, '진짜 해지 못 함 인정합니다ㅋㅋ', NOW() - INTERVAL '5 hours 53 minutes', NOW(), 1, 1, false),
(54, 1, 360, '중독성 있어요 배송 올 때마다 행복함', NOW() - INTERVAL '5 hours 51 minutes', NOW(), 1, 1, false),
(54, 1, 360, '저도 한 달만 할 생각이었는데 벌써 1년째', NOW() - INTERVAL '5 hours 49 minutes', NOW(), 1, 1, false);

-- 좋아요 벌크 데이터 (댓글 좋아요 기능 테스트용)
INSERT INTO his_hot_deal_comment_like (comment_id, user_id) VALUES
(311, 1), (313, 1), (322, 1), (328, 1), (343, 1), (360, 1);

-- 좋아요 카운트 업데이트
UPDATE mst_hot_deal_comment SET like_count = 1 WHERE id IN (311, 313, 322, 328, 343, 360);
