package dev.hddc.domains.hotdeal.application.service.query

import dev.hddc.domains.hotdeal.application.ports.input.query.AdminHotDealPageResult
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealAdminQueryUsecase
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HotDealAdminQueryService(
    private val hotDealQueryPort: HotDealQueryPort,
    private val userQueryPort: UserQueryPort,
) : HotDealAdminQueryUsecase {

    @Transactional(readOnly = true)
    override fun getAll(pageable: Pageable): AdminHotDealPageResult {
        val deals = hotDealQueryPort.findAll(pageable)
        val nicknames = userQueryPort.findNicknamesByIds(deals.userIds())
        return AdminHotDealPageResult.of(deals, nicknames)
    }
}
