package dev.hddc.domains.hotdeal.application.ports.output.command

interface HotDealClickPort {
    fun isDuplicate(dealId: Long, userId: Long?, ip: String): Boolean
    fun save(dealId: Long, userId: Long?, ip: String)
}
