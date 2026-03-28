package dev.hddc.domains.hotdeal.application.ports.input.command

interface DealExpiredVoteUsecase {
    fun vote(userId: Long, dealId: Long)
    fun unvote(userId: Long, dealId: Long)
}
