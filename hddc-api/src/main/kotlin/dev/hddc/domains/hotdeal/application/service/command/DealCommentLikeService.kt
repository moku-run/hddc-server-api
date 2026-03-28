package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealCommentLikeUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentLikePort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealCommentLikeQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealCommentQueryPort
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentLikeModel
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DealCommentLikeService(
    private val hotDealCommentQueryPort: HotDealCommentQueryPort,
    private val hotDealCommentPort: HotDealCommentPort,
    private val hotDealCommentLikePort: HotDealCommentLikePort,
    private val hotDealCommentLikeQueryPort: HotDealCommentLikeQueryPort,
) : DealCommentLikeUsecase {

    @Transactional
    override fun like(userId: Long, commentId: Long) {
        val comment = hotDealCommentQueryPort.loadById(commentId)
        if (comment.isDeleted) throw BusinessException(ApiResponseCode.HOT_DEAL_COMMENT_NOT_FOUND)
        if (hotDealCommentLikeQueryPort.existsByCommentIdAndUserId(commentId, userId)) return

        hotDealCommentLikePort.save(HotDealCommentLikeModel(commentId = commentId, userId = userId))
        hotDealCommentPort.updateLikeCount(commentId, comment.incrementedLikeCount())
    }

    @Transactional
    override fun unlike(userId: Long, commentId: Long) {
        val comment = hotDealCommentQueryPort.loadById(commentId)
        val like = hotDealCommentLikeQueryPort.findByCommentIdAndUserId(commentId, userId) ?: return

        hotDealCommentLikePort.delete(like)
        hotDealCommentPort.updateLikeCount(commentId, comment.decrementedLikeCount())
    }
}
