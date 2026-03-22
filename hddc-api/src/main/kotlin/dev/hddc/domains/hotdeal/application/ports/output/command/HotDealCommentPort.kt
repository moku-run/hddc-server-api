package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel

interface HotDealCommentPort {
    fun findById(commentId: Long): HotDealCommentModel?
    fun existsById(commentId: Long): Boolean
    fun save(model: HotDealCommentModel): HotDealCommentModel
    fun findAllByDealId(dealId: Long): List<HotDealCommentModel>
    fun findAllByDealIdIncludingDeleted(dealId: Long): List<HotDealCommentModel>
    fun findRootComments(dealId: Long, afterId: Long?, size: Int): List<HotDealCommentModel>
    fun findRepliesByParentIds(parentIds: List<Long>): List<HotDealCommentModel>
}
