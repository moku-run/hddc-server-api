package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.HotDealExpiredVoteModel

interface HotDealExpiredVotePort {
    fun save(model: HotDealExpiredVoteModel): HotDealExpiredVoteModel
    fun delete(model: HotDealExpiredVoteModel)
}
