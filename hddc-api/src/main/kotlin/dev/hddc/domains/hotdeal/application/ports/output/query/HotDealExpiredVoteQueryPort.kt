package dev.hddc.domains.hotdeal.application.ports.output.query

import dev.hddc.domains.hotdeal.domain.model.HotDealExpiredVoteModel

interface HotDealExpiredVoteQueryPort {
    fun existsByDealIdAndUserId(dealId: Long, userId: Long): Boolean
    fun findByDealIdAndUserId(dealId: Long, userId: Long): HotDealExpiredVoteModel?
    fun findAllByUserIdAndDealIds(userId: Long, dealIds: List<Long>): List<HotDealExpiredVoteModel>
}
