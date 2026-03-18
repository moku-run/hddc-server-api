package dev.hddc.domains.hotdeal.application.ports.output.query

import dev.hddc.domains.hotdeal.domain.model.HotDealModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface HotDealQueryPort {
    fun findActive(pageable: Pageable): Page<HotDealModel>
    fun search(query: String, pageable: Pageable): Page<HotDealModel>
}
