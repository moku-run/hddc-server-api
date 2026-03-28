package dev.hddc.domains.hotdeal.adapter.out.command

import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.CandidateDealEntity
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.CandidateDealRepository
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.loadById
import dev.hddc.domains.hotdeal.application.ports.output.command.CandidateDealPort
import dev.hddc.domains.hotdeal.application.ports.output.query.CandidateDealPageData
import dev.hddc.domains.hotdeal.application.ports.output.query.CandidateDealQueryPort
import dev.hddc.domains.hotdeal.domain.model.CandidateDealModel
import dev.hddc.domains.hotdeal.domain.model.CandidateDealStatus
import dev.hddc.framework.pagination.Pagination
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class CandidateDealCommandAdapter(
    private val candidateDealRepository: CandidateDealRepository,
) : CandidateDealPort, CandidateDealQueryPort {

    override fun findByStatus(status: String, pageable: Pageable): CandidateDealPageData {
        val sorted = PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by(Sort.Direction.DESC, "crawledAt"))
        val result = candidateDealRepository.findByStatus(status, sorted)
        return CandidateDealPageData(
            content = result.content.map { it.toDomain() },
            pagination = Pagination.of(result),
        )
    }

    override fun findById(id: Long): CandidateDealModel? =
        candidateDealRepository.findById(id).orElse(null)?.toDomain()

    override fun loadById(id: Long): CandidateDealModel =
        candidateDealRepository.loadById(id).toDomain()

    override fun findAllByIdsAndStatus(ids: List<Long>, status: String): List<CandidateDealModel> =
        candidateDealRepository.findAllByIdInAndStatus(ids, status).map { it.toDomain() }

    override fun updateStatus(id: Long, status: String, transferredAt: Instant?) {
        val entity = candidateDealRepository.loadById(id)
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
        status = CandidateDealStatus.valueOf(status),
        crawledAt = crawledAt,
        transferredAt = transferredAt,
    )
}
