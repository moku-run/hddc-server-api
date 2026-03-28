package dev.hddc.domains.hotdeal.application.ports.input.query

import org.springframework.data.domain.Pageable

interface HotDealAdminQueryUsecase {
    fun getAll(pageable: Pageable): AdminHotDealPageResult
}
