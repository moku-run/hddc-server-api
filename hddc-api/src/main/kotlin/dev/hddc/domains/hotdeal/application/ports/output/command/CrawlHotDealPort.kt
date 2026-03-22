package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.CrawlHotDealModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CrawlHotDealPort {
    fun findByStatus(status: String, pageable: Pageable): Page<CrawlHotDealModel>
    fun findById(id: Long): CrawlHotDealModel?
    fun findAllByIdsAndStatus(ids: List<Long>, status: String): List<CrawlHotDealModel>
    fun updateStatus(id: Long, status: String, transferredAt: java.time.Instant? = null)
}
