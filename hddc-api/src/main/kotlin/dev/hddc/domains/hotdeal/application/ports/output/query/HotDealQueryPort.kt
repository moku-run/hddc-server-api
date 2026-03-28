package dev.hddc.domains.hotdeal.application.ports.output.query

import dev.hddc.domains.hotdeal.domain.model.HotDealModel
import dev.hddc.framework.pagination.Pagination
import org.springframework.data.domain.Pageable

interface HotDealQueryPort {
    fun findById(dealId: Long): HotDealModel?
    fun loadById(dealId: Long): HotDealModel
    fun findActive(sort: String, pageable: Pageable): HotDealPageData
    fun search(query: String, pageable: Pageable): HotDealPageData
    fun findAll(pageable: Pageable): HotDealPageData
}

data class HotDealPageData(
    val content: List<HotDealModel>,
    val pagination: Pagination,
) {
    fun userIds(): List<Long> = content.map { it.userId }.distinct()
}
