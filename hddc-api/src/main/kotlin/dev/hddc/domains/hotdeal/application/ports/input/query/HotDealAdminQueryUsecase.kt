package dev.hddc.domains.hotdeal.application.ports.input.query

interface HotDealAdminQueryUsecase {
    fun getAll(page: Int, size: Int): AdminHotDealPageResult
}
