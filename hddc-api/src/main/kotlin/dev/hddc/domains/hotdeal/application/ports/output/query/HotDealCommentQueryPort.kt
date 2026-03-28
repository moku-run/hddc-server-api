package dev.hddc.domains.hotdeal.application.ports.output.query

import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel

interface HotDealCommentQueryPort {
    fun loadById(commentId: Long): HotDealCommentModel
    fun findRootComments(dealId: Long, afterId: Long?, size: Int): List<HotDealCommentModel>
    fun findRepliesByParentIds(parentIds: List<Long>): List<HotDealCommentModel>
}
