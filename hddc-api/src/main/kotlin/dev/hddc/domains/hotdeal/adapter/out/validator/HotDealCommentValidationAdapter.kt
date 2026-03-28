package dev.hddc.domains.hotdeal.adapter.out.validator

import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealCommentQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.validator.HotDealCommentValidator
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.BusinessException
import org.springframework.stereotype.Component

@Component
class HotDealCommentValidationAdapter(
    private val hotDealCommentQueryPort: HotDealCommentQueryPort,
) : HotDealCommentValidator {

    override fun validateParentComment(parentId: Long, dealId: Long) {
        val parent = hotDealCommentQueryPort.loadById(parentId)
        if (!parent.belongsTo(dealId) || parent.isDeleted) {
            throw BusinessException(ApiResponseCode.HOT_DEAL_COMMENT_NOT_FOUND)
        }
    }

    override fun validateCommentOwnership(commentId: Long, userId: Long, dealId: Long) {
        val comment = hotDealCommentQueryPort.loadById(commentId)
        if (!comment.belongsTo(dealId) || !comment.isOwnedBy(userId) || comment.isDeleted) {
            throw BusinessException(ApiResponseCode.HOT_DEAL_COMMENT_NOT_FOUND)
        }
    }

    override fun validateNotDeleted(commentId: Long) {
        val comment = hotDealCommentQueryPort.loadById(commentId)
        if (comment.isDeleted) {
            throw BusinessException(ApiResponseCode.HOT_DEAL_COMMENT_NOT_FOUND)
        }
    }
}
