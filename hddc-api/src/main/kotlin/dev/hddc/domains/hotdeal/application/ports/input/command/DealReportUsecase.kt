package dev.hddc.domains.hotdeal.application.ports.input.command

interface DealReportUsecase {
    fun reportDeal(userId: Long, dealId: Long, reason: String)
    fun reportComment(userId: Long, dealId: Long, commentId: Long, reason: String)
}
