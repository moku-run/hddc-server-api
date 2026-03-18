package dev.hddc.domains.hotdeal.application.ports.input.query

import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel

interface HotDealQueryUsecase {
    fun getDeals(userId: Long?, sort: String, page: Int, size: Int): HotDealPageResult
    fun search(userId: Long?, query: String, page: Int, size: Int): HotDealPageResult
    fun getComments(dealId: Long): List<HotDealCommentModel>
}
