package dev.hddc.domains.hotdeal.application.ports.input.command

interface DealClickSyncUsecase {
    fun sync(userId: Long, dealIds: List<Long>): Int
}
