package dev.hddc.domains.hotdeal.application.ports.input.command

import dev.hddc.domains.hotdeal.domain.model.CrawlHotDealModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

data class ApproveResult(val approvedCount: Int)

interface CrawlDealAdminUsecase {
    fun getCrawlDeals(status: String, pageable: Pageable): Page<CrawlHotDealModel>
    fun approve(crawlDealId: Long): Long
    fun reject(crawlDealId: Long)
    fun bulkApprove(crawlDealIds: List<Long>): ApproveResult
}
