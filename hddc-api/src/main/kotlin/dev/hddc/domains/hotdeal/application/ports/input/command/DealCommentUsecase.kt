package dev.hddc.domains.hotdeal.application.ports.input.command

import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel

interface DealCommentUsecase {
    fun addComment(userId: Long, dealId: Long, content: String, parentId: Long?): HotDealCommentModel
    fun deleteComment(userId: Long, dealId: Long, commentId: Long)
}
