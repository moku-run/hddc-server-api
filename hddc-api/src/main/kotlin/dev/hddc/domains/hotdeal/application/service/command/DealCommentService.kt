package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealCommentUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentPort
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DealCommentService(
    private val hotDealCommandPort: HotDealCommandPort,
    private val hotDealCommentPort: HotDealCommentPort,
) : DealCommentUsecase {

    @Transactional
    override fun addComment(userId: Long, dealId: Long, content: String, parentId: Long?): HotDealCommentModel {
        val deal = hotDealCommandPort.findById(dealId)
            ?: throw IllegalArgumentException(ApiResponseCode.HOT_DEAL_NOT_FOUND.code)

        if (parentId != null) {
            val parent = hotDealCommentPort.findById(parentId)
                ?: throw IllegalArgumentException(ApiResponseCode.HOT_DEAL_COMMENT_NOT_FOUND.code)
            require(parent.dealId == dealId && !parent.isDeleted) {
                ApiResponseCode.HOT_DEAL_COMMENT_NOT_FOUND.code
            }
        }

        val model = HotDealCommentModel(
            dealId = dealId,
            userId = userId,
            parentId = parentId,
            content = content,
        )
        val saved = hotDealCommentPort.save(model)

        hotDealCommandPort.save(deal.copy(commentCount = deal.commentCount + 1))

        return saved
    }

    @Transactional
    override fun deleteComment(userId: Long, dealId: Long, commentId: Long) {
        val deal = hotDealCommandPort.findById(dealId)
            ?: throw IllegalArgumentException(ApiResponseCode.HOT_DEAL_NOT_FOUND.code)
        val comment = hotDealCommentPort.findById(commentId)
            ?: throw IllegalArgumentException(ApiResponseCode.HOT_DEAL_COMMENT_NOT_FOUND.code)
        require(comment.dealId == dealId && comment.userId == userId && !comment.isDeleted) {
            ApiResponseCode.HOT_DEAL_COMMENT_NOT_FOUND.code
        }

        hotDealCommentPort.save(comment.copy(isDeleted = true))
        hotDealCommandPort.save(deal.copy(commentCount = maxOf(0, deal.commentCount - 1)))
    }
}
