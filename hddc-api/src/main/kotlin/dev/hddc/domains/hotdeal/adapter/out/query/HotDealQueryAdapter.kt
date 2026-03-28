package dev.hddc.domains.hotdeal.adapter.out.query

import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.HotDealEntity
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.mapper.toDomain
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.HotDealRepository
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.loadById
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealPageData
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.domain.model.HotDealModel
import dev.hddc.framework.pagination.Pagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class HotDealQueryAdapter(
    private val hotDealRepository: HotDealRepository,
) : HotDealQueryPort {

    override fun findById(dealId: Long): HotDealModel? =
        hotDealRepository.findById(dealId).orElse(null)?.toDomain()

    override fun loadById(dealId: Long): HotDealModel =
        hotDealRepository.loadById(dealId).toDomain()

    override fun findActive(sort: String, pageable: Pageable): HotDealPageData {
        val sorted = PageRequest.of(pageable.pageNumber, pageable.pageSize, resolveSort(sort))
        return hotDealRepository.findByIsDeletedFalseAndIsExpiredFalse(sorted).toPageData()
    }

    override fun search(query: String, pageable: Pageable): HotDealPageData =
        hotDealRepository.search(query, pageable).toPageData()

    override fun findAll(pageable: Pageable): HotDealPageData =
        hotDealRepository.findAll(pageable).toPageData()

    private fun Page<HotDealEntity>.toPageData() = HotDealPageData(
        content = content.map { it.toDomain() },
        pagination = Pagination.of(this),
    )

    private fun resolveSort(sort: String): Sort = when (sort) {
        "popular" -> Sort.by(Sort.Direction.DESC, "likeCount").and(Sort.by(Sort.Direction.DESC, "createdAt"))
        "discount" -> Sort.by(Sort.Direction.DESC, "discountRate").and(Sort.by(Sort.Direction.DESC, "createdAt"))
        else -> Sort.by(Sort.Direction.DESC, "createdAt")
    }
}
