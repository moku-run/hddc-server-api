package dev.hddc.domains.hotdeal.application.service.query

import dev.hddc.domains.hotdeal.application.ports.input.query.AdminHotDealPageResult
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealAdminQueryUsecase
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealWithNickname
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HotDealAdminQueryService(
    private val hotDealQueryPort: HotDealQueryPort,
    private val userQueryPort: UserQueryPort,
    private val dealNicknameEnricher: DealNicknameEnricher,
    private val adminPageBuilder: AdminHotDealPageBuilder,
) : HotDealAdminQueryUsecase {

    // ──────────────────────────────────────────────
    // 현재 코드 (before)
    // ──────────────────────────────────────────────
    @Transactional(readOnly = true)
    override fun getAll(page: Int, size: Int): AdminHotDealPageResult {
        val data = hotDealQueryPort.findAll(page, size)
        val userIds = data.content.map { it.userId }.distinct()
        val nicknames = userQueryPort.findNicknamesByIds(userIds)
        return AdminHotDealPageResult(
            content = data.content.mapIndexed { index, deal ->
                HotDealWithNickname(
                    deal = deal,
                    nickname = nicknames[deal.userId] ?: "알 수 없음",
                    dealNumber = data.pagination.totalItems - ((data.pagination.currentPage - 1).toLong() * data.pagination.perPage) - index,
                )
            },
            pagination = data.pagination,
        )
    }

    // ──────────────────────────────────────────────
    // getAll1: Port가 enriched 결과를 직접 반환
    // ──────────────────────────────────────────────
    // Port 하나가 JOIN된 결과(닉네임 포함)를 반환
    // 장점: 서비스 1줄
    // 단점: QueryPort가 user 테이블까지 알아야 함 (크로스 도메인 결합)
    fun getAll1(page: Int, size: Int): AdminHotDealPageResult =
        AdminHotDealPageResult.from(hotDealQueryPort.findAllWithNicknames(page, size))

    // ──────────────────────────────────────────────
    // getAll2: 단계별 Port 호출, 조합은 결과 타입 companion factory
    // ──────────────────────────────────────────────
    // HotDealPageData.userIds() 편의 메서드 + AdminHotDealPageResult.of() factory
    // 장점: 서비스 3줄, 가공은 결과 타입에 캡슐화
    // 단점: 결과 타입에 조합 로직 포함
    fun getAll2(page: Int, size: Int): AdminHotDealPageResult {
        val deals = hotDealQueryPort.findAll(page, size)
        val nicknames = userQueryPort.findNicknamesByIds(deals.userIds())
        return AdminHotDealPageResult.of(deals, nicknames)
    }

    // ──────────────────────────────────────────────
    // getAll3: Enricher 별도 클래스
    // ──────────────────────────────────────────────
    // DealNicknameEnricher가 닉네임 붙이기 전담 (재사용 가능)
    // 장점: AdminQuery, PublicQuery 모두에서 enricher 재사용
    // 단점: 클래스 하나 더
    fun getAll3(page: Int, size: Int): AdminHotDealPageResult {
        val deals = hotDealQueryPort.findAll(page, size)
        return dealNicknameEnricher.enrichForAdmin(deals)
    }

    // ──────────────────────────────────────────────
    // getAll4: Result Builder 패턴
    // ──────────────────────────────────────────────
    // Builder가 mapIndexed + dealNumber 계산 담당
    // 장점: 서비스는 데이터 수집만
    // 단점: getAll2와 유사하지만 별도 Builder 클래스
    fun getAll4(page: Int, size: Int): AdminHotDealPageResult {
        val deals = hotDealQueryPort.findAll(page, size)
        val nicknames = userQueryPort.findNicknamesByIds(deals.userIds())
        return adminPageBuilder.build(deals, nicknames)
    }

    // ──────────────────────────────────────────────
    // getAll5: Port가 Admin용 Result를 직접 반환 (DB JOIN)
    // ──────────────────────────────────────────────
    // Adapter에서 hot_deal JOIN mst_user 쿼리로 닉네임 포함
    // 장점: 서비스 1줄, N+1 없음
    // 단점: QueryPort가 AdminHotDealPageResult를 알아야 함, user 테이블 직접 JOIN
    fun getAll5(page: Int, size: Int): AdminHotDealPageResult =
        AdminHotDealPageResult.from(hotDealQueryPort.findAllWithNicknames(page, size))
}
