package dev.hddc.domains.hotdeal.application.ports.output.query

import dev.hddc.domains.hotdeal.domain.model.HotDealModel
import dev.hddc.framework.pagination.Pagination

interface HotDealQueryPort {
    fun findById(dealId: Long): HotDealModel?
    fun loadById(dealId: Long): HotDealModel
    fun findActive(sort: String, page: Int, size: Int): HotDealPageData
    fun search(query: String, page: Int, size: Int): HotDealPageData
    fun findAll(page: Int, size: Int): HotDealPageData
}

data class HotDealPageData(
    val content: List<HotDealModel>,
    val pagination: Pagination,
) {
    fun userIds(): List<Long> = content.map { it.userId }.distinct()
}
