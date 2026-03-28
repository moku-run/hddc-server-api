package dev.hddc.domains.hotdeal.adapter.out.command

import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.HotDealCommentEntity
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.mapper.toDomain
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.HotDealCommentRepository
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.loadById
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealCommentQueryPort
import dev.hddc.domains.hotdeal.domain.model.CreateHotDealCommentModel
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class HotDealCommentCommandAdapter(
    private val hotDealCommentRepository: HotDealCommentRepository,
) : HotDealCommentPort, HotDealCommentQueryPort {

    override fun create(model: CreateHotDealCommentModel): HotDealCommentModel {
        val entity = HotDealCommentEntity(
            dealId = model.dealId,
            userId = model.userId,
            parentId = model.parentId,
            content = model.content,
        )
        return hotDealCommentRepository.save(entity).toDomain()
    }

    override fun loadById(commentId: Long): HotDealCommentModel =
        hotDealCommentRepository.loadById(commentId).toDomain()

    override fun softDelete(commentId: Long) {
        val entity = hotDealCommentRepository.loadById(commentId)
        entity.softDelete()
        hotDealCommentRepository.save(entity)
    }

    override fun updateLikeCount(commentId: Long, count: Int) {
        val entity = hotDealCommentRepository.loadById(commentId)
        entity.likeCount = count
        hotDealCommentRepository.save(entity)
    }

    override fun findRootComments(dealId: Long, afterId: Long?, size: Int): List<HotDealCommentModel> {
        val pageable = PageRequest.of(0, size)
        val entities = if (afterId != null) {
            hotDealCommentRepository.findRootCommentsAfter(dealId, afterId, pageable)
        } else {
            hotDealCommentRepository.findRootComments(dealId, pageable)
        }
        return entities.map { it.toDomain() }
    }

    override fun findRepliesByParentIds(parentIds: List<Long>): List<HotDealCommentModel> {
        if (parentIds.isEmpty()) return emptyList()
        return hotDealCommentRepository.findAllByParentIdInOrderByCreatedAtAsc(parentIds)
            .map { it.toDomain() }
    }
}
