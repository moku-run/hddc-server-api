package dev.hddc.domains.hotdeal.adapter.out.persistence

import dev.hddc.domains.hotdeal.application.ports.output.command.CandidateDealPageData
import dev.hddc.domains.hotdeal.application.ports.output.command.CandidateDealPort
import dev.hddc.domains.hotdeal.domain.model.CandidateDealModel
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class CandidateDealPersistenceAdapter(
    private val candidateDealRepository: CandidateDealRepository,
) : CandidateDealPort {

    override fun findByStatus(status: String, page: Int, size: Int): CandidateDealPageData {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "crawledAt"))
        val result = candidateDealRepository.findByStatus(status, pageable)
        return CandidateDealPageData(
            content = result.content.map { it.toDomain() },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages,
        )
    }

    override fun findById(id: Long): CandidateDealModel? =
        candidateDealRepository.findById(id).orElse(null)?.toDomain()

    override fun findAllByIdsAndStatus(ids: List<Long>, status: String): List<CandidateDealModel> =
        candidateDealRepository.findAllByIdInAndStatus(ids, status).map { it.toDomain() }

    override fun updateStatus(id: Long, status: String, transferredAt: Instant?) {
        val entity = candidateDealRepository.findById(id).orElse(null) ?: return
        entity.status = status
        entity.transferredAt = transferredAt
        candidateDealRepository.save(entity)
    }

    private fun CandidateDealEntity.toDomain() = CandidateDealModel(
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
