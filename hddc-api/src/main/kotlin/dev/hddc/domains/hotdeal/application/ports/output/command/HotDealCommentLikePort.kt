package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.HotDealCommentLikeModel

interface HotDealCommentLikePort {
    fun existsByCommentIdAndUserId(commentId: Long, userId: Long): Boolean
    fun findByCommentIdAndUserId(commentId: Long, userId: Long): HotDealCommentLikeModel?
    fun save(model: HotDealCommentLikeModel): HotDealCommentLikeModel
    fun delete(model: HotDealCommentLikeModel)
    fun findAllByUserIdAndCommentIds(userId: Long, commentIds: List<Long>): List<HotDealCommentLikeModel>
}
