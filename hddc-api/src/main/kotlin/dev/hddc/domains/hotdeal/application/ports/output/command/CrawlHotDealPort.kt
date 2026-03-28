package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.CrawlHotDealModel

data class CrawlHotDealPageData(
    val content: List<CrawlHotDealModel>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

interface CrawlHotDealPort {
    fun findByStatus(status: String, page: Int, size: Int): CrawlHotDealPageData
    fun findById(id: Long): CrawlHotDealModel?
    fun findAllByIdsAndStatus(ids: List<Long>, status: String): List<CrawlHotDealModel>
    fun updateStatus(id: Long, status: String, transferredAt: java.time.Instant? = null)
}
