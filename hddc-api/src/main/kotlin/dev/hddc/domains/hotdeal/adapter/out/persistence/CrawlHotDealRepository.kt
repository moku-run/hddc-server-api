package dev.hddc.domains.hotdeal.adapter.out.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CrawlHotDealRepository : JpaRepository<CrawlHotDealEntity, Long> {
    fun findByStatus(status: String, pageable: Pageable): Page<CrawlHotDealEntity>
    fun findAllByIdInAndStatus(ids: List<Long>, status: String): List<CrawlHotDealEntity>
}
