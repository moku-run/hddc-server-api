package dev.hddc.domains.hotdeal.application.ports.input.command

interface DealLikeUsecase {
    fun like(userId: Long, dealId: Long)
    fun unlike(userId: Long, dealId: Long)
}
