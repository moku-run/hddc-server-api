package dev.hddc.domains.hotdeal.application.ports.output.query

import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealPageResult
import dev.hddc.domains.hotdeal.domain.model.HotDealModel

interface HotDealQueryPort {
    fun findActive(sort: String, page: Int, size: Int): HotDealPageData
    fun search(query: String, page: Int, size: Int): HotDealPageData
    fun findAll(page: Int, size: Int): HotDealPageData
}

data class HotDealPageData(
    val content: List<HotDealModel>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)
