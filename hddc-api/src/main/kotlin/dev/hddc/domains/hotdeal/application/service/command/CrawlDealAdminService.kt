package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.ApproveResult
import dev.hddc.domains.hotdeal.application.ports.input.command.CrawlDealAdminUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.CrawlHotDealPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.domain.model.CrawlHotDealModel
import dev.hddc.domains.hotdeal.domain.model.CreateHotDealModel
import dev.hddc.domains.hotdeal.domain.spec.HotDealSpec
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class CrawlDealAdminService(
    private val crawlHotDealPort: CrawlHotDealPort,
    private val hotDealCommandPort: HotDealCommandPort,
) : CrawlDealAdminUsecase {

    @Transactional(readOnly = true)
    override fun getCrawlDeals(status: String, pageable: Pageable): Page<CrawlHotDealModel> =
        crawlHotDealPort.findByStatus(status, pageable)

    @Transactional
    override fun approve(crawlDealId: Long): Long {
        val crawl = crawlHotDealPort.findById(crawlDealId)
            ?: throw IllegalArgumentException("CRAWL_DEAL_NOT_FOUND")

        require(crawl.status == "PENDING") {
            "INVALID_REQUEST"
        }

        val saved = hotDealCommandPort.create(transferToCreateModel(crawl))

        crawlHotDealPort.updateStatus(crawlDealId, "APPROVED", Instant.now())

        return saved.id
    }

    @Transactional
    override fun reject(crawlDealId: Long) {
        val crawl = crawlHotDealPort.findById(crawlDealId)
            ?: throw IllegalArgumentException("CRAWL_DEAL_NOT_FOUND")

        require(crawl.status == "PENDING") {
            "INVALID_REQUEST"
        }

        crawlHotDealPort.updateStatus(crawlDealId, "REJECTED")
    }

    @Transactional
    override fun bulkApprove(crawlDealIds: List<Long>): ApproveResult {
        val crawls = crawlHotDealPort.findAllByIdsAndStatus(crawlDealIds, "PENDING")

        crawls.forEach { crawl ->
            hotDealCommandPort.create(transferToCreateModel(crawl))
            crawlHotDealPort.updateStatus(crawl.id!!, "APPROVED", Instant.now())
        }

        return ApproveResult(approvedCount = crawls.size)
    }

    private fun transferToCreateModel(crawl: CrawlHotDealModel) = CreateHotDealModel(
        userId = HotDealSpec.SYSTEM_USER_ID,
        title = crawl.title ?: "제목 없음",
        description = crawl.description,
        url = crawl.dealLink ?: crawl.postUrl,
        imageUrl = crawl.imageUrl,
        originalPrice = crawl.originalPrice,
        dealPrice = crawl.dealPrice,
        discountRate = crawl.discountRate,
        category = crawl.category,
        store = crawl.store,
    )
}
