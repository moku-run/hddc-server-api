package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.CreateHotDealCommentModel
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel

interface HotDealCommentPort {
    fun create(model: CreateHotDealCommentModel): HotDealCommentModel
    fun softDelete(commentId: Long)
    fun updateLikeCount(commentId: Long, count: Int)
}
