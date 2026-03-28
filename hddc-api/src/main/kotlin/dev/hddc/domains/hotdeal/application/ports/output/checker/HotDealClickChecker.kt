package dev.hddc.domains.hotdeal.application.ports.output.checker

interface HotDealClickChecker {
    fun isDuplicate(dealId: Long, userId: Long?, ip: String): Boolean
}
