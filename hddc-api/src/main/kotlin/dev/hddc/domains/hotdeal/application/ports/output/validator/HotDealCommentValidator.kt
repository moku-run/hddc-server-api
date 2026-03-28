package dev.hddc.domains.hotdeal.application.ports.output.validator

interface HotDealCommentValidator {
    fun validateParentComment(parentId: Long, dealId: Long)
    fun validateCommentOwnership(commentId: Long, userId: Long, dealId: Long)
    fun validateNotDeleted(commentId: Long)
}
