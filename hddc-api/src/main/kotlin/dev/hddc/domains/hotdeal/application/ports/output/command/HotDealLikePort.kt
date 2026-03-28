package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.HotDealLikeModel

interface HotDealLikePort {
    fun save(model: HotDealLikeModel): HotDealLikeModel
    fun delete(model: HotDealLikeModel)
}
