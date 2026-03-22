package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealCommentLikeUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentLikePort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentPort
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentLikeModel
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DealCommentLikeService(
    private val hotDealCommentPort: HotDealCommentPort,
    private val hotDealCommentLikePort: HotDealCommentLikePort,
) : DealCommentLikeUsecase {

    @Transactional
    override fun like(userId: Long, commentId: Long) {
        val comment = hotDealCommentPort.findById(commentId)
            ?: throw IllegalArgumentException(ApiResponseCode.HOT_DEAL_COMMENT_NOT_FOUND.code)
        if (comment.isDeleted) throw IllegalArgumentException(ApiResponseCode.HOT_DEAL_COMMENT_NOT_FOUND.code)
        if (hotDealCommentLikePort.existsByCommentIdAndUserId(commentId, userId)) return

        hotDealCommentLikePort.save(HotDealCommentLikeModel(commentId = commentId, userId = userId))
        hotDealCommentPort.save(comment.copy(likeCount = comment.likeCount + 1))
    }

    @Transactional
    override fun unlike(userId: Long, commentId: Long) {
        val comment = hotDealCommentPort.findById(commentId)
            ?: throw IllegalArgumentException(ApiResponseCode.HOT_DEAL_COMMENT_NOT_FOUND.code)
        val like = hotDealCommentLikePort.findByCommentIdAndUserId(commentId, userId) ?: return

        hotDealCommentLikePort.delete(like)
        hotDealCommentPort.save(comment.copy(likeCount = maxOf(0, comment.likeCount - 1)))
    }
}
