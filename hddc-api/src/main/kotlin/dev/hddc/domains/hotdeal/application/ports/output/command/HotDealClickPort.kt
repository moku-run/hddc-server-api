package dev.hddc.domains.hotdeal.application.ports.output.command

interface HotDealClickPort {
    fun save(dealId: Long, userId: Long?, ip: String)
}
