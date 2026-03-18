package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.HotDealExpiredVoteModel

interface HotDealExpiredVotePort {
    fun existsByDealIdAndUserId(dealId: Long, userId: Long): Boolean
    fun findByDealIdAndUserId(dealId: Long, userId: Long): HotDealExpiredVoteModel?
    fun findAllByUserIdAndDealIds(userId: Long, dealIds: List<Long>): List<HotDealExpiredVoteModel>
    fun save(model: HotDealExpiredVoteModel): HotDealExpiredVoteModel
    fun delete(model: HotDealExpiredVoteModel)
}
