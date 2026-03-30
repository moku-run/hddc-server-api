package dev.hddc.domains.hotdeal.application.ports.output.query

interface HotDealClickQueryPort {
    fun findDealIdsByUserIdAndDealIds(userId: Long, dealIds: List<Long>): Set<Long>
}
