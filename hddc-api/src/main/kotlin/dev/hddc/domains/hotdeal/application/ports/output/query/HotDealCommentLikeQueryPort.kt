package dev.hddc.domains.hotdeal.application.ports.output.query

import dev.hddc.domains.hotdeal.domain.model.HotDealCommentLikeModel

interface HotDealCommentLikeQueryPort {
    fun existsByCommentIdAndUserId(commentId: Long, userId: Long): Boolean
    fun findByCommentIdAndUserId(commentId: Long, userId: Long): HotDealCommentLikeModel?
    fun findAllByUserIdAndCommentIds(userId: Long, commentIds: List<Long>): List<HotDealCommentLikeModel>
}
