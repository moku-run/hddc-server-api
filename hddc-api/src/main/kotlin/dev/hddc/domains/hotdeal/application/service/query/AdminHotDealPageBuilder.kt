package dev.hddc.domains.hotdeal.application.service.query

import dev.hddc.domains.hotdeal.application.ports.input.query.AdminHotDealPageResult
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealWithNickname
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealPageData
import org.springframework.stereotype.Component

@Component
class AdminHotDealPageBuilder {
    fun build(deals: HotDealPageData, nicknames: Map<Long, String>): AdminHotDealPageResult =
        AdminHotDealPageResult(
            content = deals.content.mapIndexed { index, deal ->
                HotDealWithNickname(
                    deal = deal,
                    nickname = nicknames[deal.userId] ?: "알 수 없음",
                    dealNumber = deals.pagination.totalItems - ((deals.pagination.currentPage - 1).toLong() * deals.pagination.perPage) - index,
                )
            },
            pagination = deals.pagination,
        )
}
