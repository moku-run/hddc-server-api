package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel

interface HotDealCommentPort {
    fun findById(commentId: Long): HotDealCommentModel?
    fun existsById(commentId: Long): Boolean
    fun save(model: HotDealCommentModel): HotDealCommentModel
    fun findAllByDealId(dealId: Long): List<HotDealCommentModel>
}
