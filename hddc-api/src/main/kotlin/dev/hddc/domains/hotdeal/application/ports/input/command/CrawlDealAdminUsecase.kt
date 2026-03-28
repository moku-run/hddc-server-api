package dev.hddc.domains.hotdeal.application.ports.input.command

import dev.hddc.domains.hotdeal.application.ports.output.command.CrawlHotDealPageData

data class ApproveResult(val approvedCount: Int)

interface CrawlDealAdminUsecase {
    fun getCrawlDeals(status: String, page: Int, size: Int): CrawlHotDealPageData
    fun approve(crawlDealId: Long): Long
    fun reject(crawlDealId: Long)
    fun bulkApprove(crawlDealIds: List<Long>): ApproveResult
}
