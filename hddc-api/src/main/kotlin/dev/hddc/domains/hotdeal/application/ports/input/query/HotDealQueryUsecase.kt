package dev.hddc.domains.hotdeal.application.ports.input.query

interface HotDealQueryUsecase {
    fun getDeals(userId: Long?, sort: String, page: Int, size: Int): HotDealPageResult
    fun search(userId: Long?, query: String, page: Int, size: Int): HotDealPageResult
    fun getComments(dealId: Long, afterId: Long?, size: Int): CommentCursorResult
    fun getCommentsEnriched(dealId: Long, userId: Long?, afterId: Long?, size: Int): EnrichedCommentCursorResult
}
