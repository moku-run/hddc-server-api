package dev.hddc.domains.hotdeal.adapter.out.persistence

import dev.hddc.domains.hotdeal.application.ports.output.command.CrawlHotDealPort
import dev.hddc.domains.hotdeal.domain.model.CrawlHotDealModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class CrawlHotDealPersistenceAdapter(
    private val crawlHotDealRepository: CrawlHotDealRepository,
) : CrawlHotDealPort {

    override fun findByStatus(status: String, pageable: Pageable): Page<CrawlHotDealModel> =
        crawlHotDealRepository.findByStatus(status, pageable).map { it.toDomain() }

    override fun findById(id: Long): CrawlHotDealModel? =
        crawlHotDealRepository.findById(id).orElse(null)?.toDomain()

    override fun findAllByIdsAndStatus(ids: List<Long>, status: String): List<CrawlHotDealModel> =
        crawlHotDealRepository.findAllByIdInAndStatus(ids, status).map { it.toDomain() }

    override fun updateStatus(id: Long, status: String, transferredAt: Instant?) {
        val entity = crawlHotDealRepository.findById(id).orElse(null) ?: return
        entity.status = status
        entity.transferredAt = transferredAt
        crawlHotDealRepository.save(entity)
    }

    private fun CrawlHotDealEntity.toDomain() = CrawlHotDealModel(
        id = id,
        sourceSite = sourceSite,
        sourceId = sourceId,
        title = title,
        description = description,
        postUrl = postUrl,
        dealLink = dealLink,
        imageUrl = imageUrl,
        originalPrice = originalPrice,
        dealPrice = dealPrice,
        discountRate = discountRate,
        store = store,
        category = category,
        status = status,
        crawledAt = crawledAt,
        transferredAt = transferredAt,
    )
}
