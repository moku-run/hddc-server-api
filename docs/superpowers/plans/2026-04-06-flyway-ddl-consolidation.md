# Flyway DDL Consolidation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** bulk 테스트 데이터 제거 + ALTER TABLE 분산 관리를 CREATE TABLE 단일 파일로 통합하여 Flyway 히스토리를 정리한다.

**Architecture:** 각 테이블의 현재 최종 스키마를 CREATE TABLE 시점에 완전히 반영한다. 이후 컬럼 추가/변경은 새 버전 파일로 추가하고, 동일 버전 파일을 수정하지 않는다. V024(candidate_hot_deal)는 논의 중이므로 이번 작업에서 제외한다.

**Tech Stack:** PostgreSQL, Flyway (Gradle), Spring Boot 3.4.5

---

## 파일 변경 요약

| 작업 | 파일 |
|------|------|
| **수정** | `V001__init.sql` — TIMESTAMPTZ + audit cols + unique index 통합 |
| **수정** | `V002__profile.sql` — font_color, link_round, decoration fields, link product fields 통합 |
| **삭제** (ALTER 통합) | `V007`, `V008`, `V009`, `V010`, `V012`, `V022` |
| **삭제** (bulk data) | `V013`, `V014`, `V017`, `V018`, `V021` |
| **삭제** (data fix) | `V023` — bulk data 삭제로 불필요 |
| **리네이밍** | `V011→V007`, `V015→V008`, `V016→V009`, `V019→V010`, `V020→V011` |
| **유지** | `V003`, `V004`, `V005`, `V006`, `V024` (변경 없음) |

### 리네이밍 후 최종 버전 순서

```
V001 mst_user (full DDL)
V002 profile (full DDL)
V003 analytics
V004 hot_deal
V005 hot_deal_dedup_index
V006 system_user
V007 profile_report          (← 구 V011)
V008 admin_table             (← 구 V015)
V009 update_admin_password   (← 구 V016)
V010 hot_deal_click          (← 구 V019)
V011 hot_deal_comment_like   (← 구 V020)
V024 candidate_hot_deal      (논의 중, 변경 없음)
```

---

## Task 1: bulk data 파일 + data fix 파일 삭제

**Files:**
- Delete: `V013__bulk_hot_deals.sql`
- Delete: `V014__bulk_comments.sql`
- Delete: `V017__bulk_comments_deal54.sql`
- Delete: `V018__bulk_replies_deal54.sql`
- Delete: `V021__bulk_comments_pagination_test.sql`
- Delete: `V023__fix_comment_count.sql`

- [ ] **Step 1: 6개 파일 삭제**

```bash
cd hddc-api/src/main/resources/db/migration
rm V013__bulk_hot_deals.sql \
   V014__bulk_comments.sql \
   V017__bulk_comments_deal54.sql \
   V018__bulk_replies_deal54.sql \
   V021__bulk_comments_pagination_test.sql \
   V023__fix_comment_count.sql
```

- [ ] **Step 2: 잔여 파일 확인**

```bash
ls V0*.sql
```

Expected: V001~V012, V015, V016, V019, V020, V022, V024 만 존재 (V013/V014/V017/V018/V021/V023 없음)

---

## Task 2: V001 — mst_user 통합 DDL 작성

**합칠 ALTER 내용:**
- V007: `last_login_at`, `created_at`, `updated_at` → `TIMESTAMPTZ`
- V008: `created_by BIGINT NOT NULL DEFAULT 0`, `updated_by BIGINT NOT NULL DEFAULT 0`, `deleted_at TIMESTAMPTZ`
- V009: `CREATE UNIQUE INDEX uq_mst_user_nickname ON mst_user (nickname) WHERE is_deleted = FALSE`

**Files:**
- Modify: `V001__init.sql`

- [ ] **Step 1: V001 전체를 아래 내용으로 교체**

```sql
-- =============================================================
-- V001: 회원
-- =============================================================

CREATE TABLE mst_user
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

COMMENT ON TABLE  mst_user                    IS '회원 마스터';
COMMENT ON COLUMN mst_user.role               IS '권한: USER / ADMIN';
COMMENT ON COLUMN mst_user.login_attempt_count IS '로그인 실패 횟수 (잠금 임계값 초과 시 is_locked=true)';
COMMENT ON COLUMN mst_user.last_login_at      IS '마지막 로그인 시각';
COMMENT ON COLUMN mst_user.deleted_at         IS '탈퇴 처리 시각';

CREATE INDEX idx_mst_user_email ON mst_user (email);
CREATE UNIQUE INDEX uq_mst_user_nickname ON mst_user (nickname) WHERE is_deleted = FALSE;
```

---

## Task 3: V002 — profile 통합 DDL 작성

**합칠 ALTER 내용 (mst_profile):**
- V010: `font_color VARCHAR(20)`
- V012: `link_round VARCHAR(10) NOT NULL DEFAULT 'sm'`
- V022: `background_texture`, `decorator1_type`, `decorator1_text`, `decorator2_type`, `decorator2_text`, `link_gradient_from`, `link_gradient_to`, `link_border_color`, `link_border_thick NOT NULL DEFAULT 'thin'`, `page_layout NOT NULL DEFAULT 'list'`

**합칠 ALTER 내용 (mst_profile_link):**
- V022: `price`, `original_price`, `discount_rate`, `store`, `category`, `clicks NOT NULL DEFAULT 0`, `likes NOT NULL DEFAULT 0`

**Files:**
- Modify: `V002__profile.sql`

- [ ] **Step 1: V002 전체를 아래 내용으로 교체**

```sql
-- =============================================================
-- V002: 프로필, 프로필 링크, 소셜 링크
-- =============================================================

-- 프로필: 유저당 1개, 테마/레이아웃 설정 포함
CREATE TABLE mst_profile
(
    id                     BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id                BIGINT       NOT NULL UNIQUE,
    slug                   VARCHAR(30)  NOT NULL UNIQUE,
    nickname               VARCHAR(20)  NOT NULL,
    bio                    VARCHAR(80),
    avatar_url             VARCHAR(1000),
    background_url         VARCHAR(1000),
    background_color       VARCHAR(20),
    font_color             VARCHAR(20),
    link_layout            VARCHAR(20)  NOT NULL DEFAULT 'list',
    link_style             VARCHAR(20)  NOT NULL DEFAULT 'fill',
    link_round             VARCHAR(10)  NOT NULL DEFAULT 'sm',
    font_family            VARCHAR(30)  NOT NULL DEFAULT 'pretendard',
    header_layout          VARCHAR(20)  NOT NULL DEFAULT 'center',
    link_animation         VARCHAR(20)  NOT NULL DEFAULT 'none',
    color_theme            VARCHAR(20)  NOT NULL DEFAULT 'default',
    custom_primary_color   VARCHAR(20),
    custom_secondary_color VARCHAR(20),
    dark_mode              BOOLEAN      NOT NULL DEFAULT FALSE,
    background_texture     VARCHAR(20),
    decorator1_type        VARCHAR(20),
    decorator1_text        VARCHAR(80),
    decorator2_type        VARCHAR(20),
    decorator2_text        VARCHAR(80),
    link_gradient_from     VARCHAR(20),
    link_gradient_to       VARCHAR(20),
    link_border_color      VARCHAR(20),
    link_border_thick      VARCHAR(20)  NOT NULL DEFAULT 'thin',
    page_layout            VARCHAR(20)  NOT NULL DEFAULT 'list',
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by             BIGINT       NOT NULL DEFAULT 0,
    updated_by             BIGINT       NOT NULL DEFAULT 0,
    is_deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at             TIMESTAMPTZ,

    CONSTRAINT fk_mst_profile_mst_user
        FOREIGN KEY (user_id) REFERENCES mst_user (id)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
);

COMMENT ON TABLE  mst_profile                  IS '프로필 마스터';
COMMENT ON COLUMN mst_profile.user_id          IS '사용자 FK';
COMMENT ON COLUMN mst_profile.slug             IS '공개 URL slug (3~30자)';
COMMENT ON COLUMN mst_profile.nickname         IS '닉네임 (2~20자)';
COMMENT ON COLUMN mst_profile.bio              IS '자기소개 (최대 80자)';
COMMENT ON COLUMN mst_profile.font_color       IS '폰트 색상 (hex)';
COMMENT ON COLUMN mst_profile.link_layout      IS '링크 레이아웃 (list, grid-2, grid-3)';
COMMENT ON COLUMN mst_profile.link_style       IS '링크 스타일 (fill, outline, shadow, rounded, pill)';
COMMENT ON COLUMN mst_profile.link_round       IS '링크 모서리 반경 (sm, md, lg, full)';
COMMENT ON COLUMN mst_profile.background_texture IS '배경 텍스처';
COMMENT ON COLUMN mst_profile.link_border_thick  IS '링크 테두리 굵기 (thin, medium, thick)';
COMMENT ON COLUMN mst_profile.page_layout        IS '페이지 레이아웃';

CREATE INDEX idx_mst_profile_user_id ON mst_profile (user_id);
CREATE INDEX idx_mst_profile_slug    ON mst_profile (slug);

-- 프로필 링크: 최대 20개
CREATE TABLE mst_profile_link
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    profile_id     BIGINT        NOT NULL,
    title          VARCHAR(100)  NOT NULL,
    url            VARCHAR(1000) NOT NULL,
    image_url      VARCHAR(1000),
    description    VARCHAR(200),
    sort_order     INT           NOT NULL DEFAULT 0,
    enabled        BOOLEAN       NOT NULL DEFAULT TRUE,
    price          BIGINT,
    original_price BIGINT,
    discount_rate  INTEGER,
    store          VARCHAR(50),
    category       VARCHAR(50),
    clicks         BIGINT        NOT NULL DEFAULT 0,
    likes          BIGINT        NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by     BIGINT        NOT NULL DEFAULT 0,
    updated_by     BIGINT        NOT NULL DEFAULT 0,
    is_deleted     BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at     TIMESTAMPTZ,

    CONSTRAINT fk_mst_profile_link_mst_profile
        FOREIGN KEY (profile_id) REFERENCES mst_profile (id)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
);

COMMENT ON TABLE  mst_profile_link              IS '프로필 링크';
COMMENT ON COLUMN mst_profile_link.profile_id   IS '프로필 FK';
COMMENT ON COLUMN mst_profile_link.title        IS '링크 제목 (최대 100자)';
COMMENT ON COLUMN mst_profile_link.description  IS '링크 설명 (최대 200자)';
COMMENT ON COLUMN mst_profile_link.sort_order   IS '정렬 순서';
COMMENT ON COLUMN mst_profile_link.price        IS '상품 가격';
COMMENT ON COLUMN mst_profile_link.original_price IS '상품 원가';
COMMENT ON COLUMN mst_profile_link.discount_rate  IS '할인율 (%)';
COMMENT ON COLUMN mst_profile_link.clicks       IS '클릭 수 (비정규화)';
COMMENT ON COLUMN mst_profile_link.likes        IS '좋아요 수 (비정규화)';

CREATE INDEX idx_mst_profile_link_profile_id ON mst_profile_link (profile_id);

-- 소셜 링크: 최대 8개
CREATE TABLE mst_profile_social
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    profile_id BIGINT        NOT NULL,
    platform   VARCHAR(30)   NOT NULL,
    url        VARCHAR(1000) NOT NULL,
    sort_order INT           NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by BIGINT        NOT NULL DEFAULT 0,
    updated_by BIGINT        NOT NULL DEFAULT 0,
    is_deleted BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,

    CONSTRAINT fk_mst_profile_social_mst_profile
        FOREIGN KEY (profile_id) REFERENCES mst_profile (id)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
);

COMMENT ON TABLE  mst_profile_social           IS '프로필 소셜 링크';
COMMENT ON COLUMN mst_profile_social.profile_id IS '프로필 FK';
COMMENT ON COLUMN mst_profile_social.platform   IS '소셜 플랫폼 (instagram, youtube, x 등)';
COMMENT ON COLUMN mst_profile_social.sort_order IS '정렬 순서';

CREATE INDEX idx_mst_profile_social_profile_id ON mst_profile_social (profile_id);
```

---

## Task 4: ALTER TABLE 파일 삭제

**Files:**
- Delete: `V007__alter_mst_user_timestamps.sql`
- Delete: `V008__add_audit_columns_to_mst_user.sql`
- Delete: `V009__add_unique_nickname.sql`
- Delete: `V010__add_font_color_to_profile.sql`
- Delete: `V012__add_link_round_to_profile.sql`
- Delete: `V022__profile_decoration_fields.sql`

- [ ] **Step 1: 6개 파일 삭제**

```bash
cd hddc-api/src/main/resources/db/migration
rm V007__alter_mst_user_timestamps.sql \
   V008__add_audit_columns_to_mst_user.sql \
   V009__add_unique_nickname.sql \
   V010__add_font_color_to_profile.sql \
   V012__add_link_round_to_profile.sql \
   V022__profile_decoration_fields.sql
```

- [ ] **Step 2: 잔여 파일 확인 — 11개만 존재해야 함**

```bash
ls V0*.sql
```

Expected (정확히 이 11개):
```
V001__init.sql
V002__profile.sql
V003__analytics.sql
V004__hot_deal.sql
V005__hot_deal_dedup_index.sql
V006__system_user.sql
V011__profile_report.sql
V015__admin_table.sql
V016__update_admin_password.sql
V019__hot_deal_click.sql
V020__hot_deal_comment_like.sql
V024__create_candidate_hot_deal.sql
```

---

## Task 5: 버전 번호 리네이밍 (gap 제거)

빈 번호를 채워 V001~V011 연속 버전으로 정리한다. V024는 그대로 유지.

**Files:**
- Rename: `V011__profile_report.sql`        → `V007__profile_report.sql`
- Rename: `V015__admin_table.sql`            → `V008__admin_table.sql`
- Rename: `V016__update_admin_password.sql`  → `V009__update_admin_password.sql`
- Rename: `V019__hot_deal_click.sql`         → `V010__hot_deal_click.sql`
- Rename: `V020__hot_deal_comment_like.sql`  → `V011__hot_deal_comment_like.sql`

- [ ] **Step 1: 5개 파일 리네이밍**

```bash
cd hddc-api/src/main/resources/db/migration
mv V011__profile_report.sql       V007__profile_report.sql
mv V015__admin_table.sql          V008__admin_table.sql
mv V016__update_admin_password.sql V009__update_admin_password.sql
mv V019__hot_deal_click.sql       V010__hot_deal_click.sql
mv V020__hot_deal_comment_like.sql V011__hot_deal_comment_like.sql
```

- [ ] **Step 2: 최종 파일 목록 확인**

```bash
ls V0*.sql
```

Expected (정확히 이 12개):
```
V001__init.sql
V002__profile.sql
V003__analytics.sql
V004__hot_deal.sql
V005__hot_deal_dedup_index.sql
V006__system_user.sql
V007__profile_report.sql
V008__admin_table.sql
V009__update_admin_password.sql
V010__hot_deal_click.sql
V011__hot_deal_comment_like.sql
V024__create_candidate_hot_deal.sql
```

---

## Task 6: flywayClean + bootRun 검증

- [ ] **Step 1: DB 완전 초기화**

```bash
./gradlew flywayClean
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 2: 서버 기동 — 12개 마이그레이션 전부 적용 확인**

```bash
./gradlew bootRun 2>&1 | grep -E "Successfully applied|Started HddcApplicationKt|ERROR|missing|violates"
```

Expected:
- `Started HddcApplicationKt in X.XXX seconds` 한 줄
- ERROR / missing / violates 없음

- [ ] **Step 3: 커밋**

```bash
git add hddc-api/src/main/resources/db/migration/
git commit -m "Refactor: Flyway DDL 통합 — bulk data 제거 + ALTER TABLE → CREATE TABLE 병합 + 버전 리넘버링"
```
