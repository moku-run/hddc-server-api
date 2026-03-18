package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.HotDealModel

interface HotDealCommandPort {
    fun findById(dealId: Long): HotDealModel?
    fun existsById(dealId: Long): Boolean
    fun save(model: HotDealModel): HotDealModel
}
