package dev.hddc.domains.hotdeal.application.ports.input.command

data class DealClickResult(
    val url: String,
    val dealId: Long,
    val clickCount: Int,
)

interface DealClickUsecase {
    fun click(dealId: Long, userId: Long?, ip: String): DealClickResult?
}
