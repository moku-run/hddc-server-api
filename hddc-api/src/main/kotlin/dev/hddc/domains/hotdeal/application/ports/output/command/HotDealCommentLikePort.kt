package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.HotDealCommentLikeModel

interface HotDealCommentLikePort {
    fun save(model: HotDealCommentLikeModel): HotDealCommentLikeModel
    fun delete(model: HotDealCommentLikeModel)
}
