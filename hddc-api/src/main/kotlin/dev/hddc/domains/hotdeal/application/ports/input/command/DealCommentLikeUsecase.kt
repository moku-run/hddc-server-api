package dev.hddc.domains.hotdeal.application.ports.input.command

interface DealCommentLikeUsecase {
    fun like(userId: Long, commentId: Long)
    fun unlike(userId: Long, commentId: Long)
}
