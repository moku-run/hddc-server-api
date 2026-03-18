package dev.hddc.domains.hotdeal.application.ports.input.command

import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel

interface DealLikeUsecase {
    fun like(userId: Long, dealId: Long)
    fun unlike(userId: Long, dealId: Long)
}

interface DealCommentUsecase {
    fun addComment(userId: Long, dealId: Long, content: String, parentId: Long?): HotDealCommentModel
    fun deleteComment(userId: Long, dealId: Long, commentId: Long)
}

interface DealExpiredVoteUsecase {
    fun vote(userId: Long, dealId: Long)
    fun unvote(userId: Long, dealId: Long)
}

interface DealReportUsecase {
    fun reportDeal(userId: Long, dealId: Long, reason: String)
    fun reportComment(userId: Long, dealId: Long, commentId: Long, reason: String)
}
