package dev.hddc.domains.hotdeal.application.ports.input.query

import org.springframework.data.domain.Pageable

interface HotDealQueryUsecase {
    fun getDeals(userId: Long?, sort: String, pageable: Pageable): HotDealPageResult
    fun search(userId: Long?, query: String, pageable: Pageable): HotDealPageResult
    fun getComments(dealId: Long, afterId: Long?, size: Int): CommentCursorResult
    fun getCommentsEnriched(dealId: Long, userId: Long?, afterId: Long?, size: Int): EnrichedCommentCursorResult
}
