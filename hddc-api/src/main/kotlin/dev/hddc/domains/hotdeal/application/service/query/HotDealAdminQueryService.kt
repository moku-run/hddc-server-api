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
) : HotDealAdminQueryUsecase {

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
                    dealNumber = data.totalElements - (data.page.toLong() * data.size) - index,
                )
            },
            page = data.page,
            size = data.size,
            totalElements = data.totalElements,
            totalPages = data.totalPages,
        )
    }
}
