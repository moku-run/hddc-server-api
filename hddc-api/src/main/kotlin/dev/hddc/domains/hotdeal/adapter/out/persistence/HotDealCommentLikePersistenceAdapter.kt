package dev.hddc.domains.hotdeal.adapter.out.persistence

import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentLikePort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealCommentLikeQueryPort
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentLikeModel
import org.springframework.stereotype.Component

@Component
class HotDealCommentLikePersistenceAdapter(
    private val hotDealCommentLikeRepository: HotDealCommentLikeRepository,
) : HotDealCommentLikePort, HotDealCommentLikeQueryPort {

    override fun existsByCommentIdAndUserId(commentId: Long, userId: Long): Boolean =
        hotDealCommentLikeRepository.existsByCommentIdAndUserId(commentId, userId)

    override fun findByCommentIdAndUserId(commentId: Long, userId: Long): HotDealCommentLikeModel? =
        hotDealCommentLikeRepository.findByCommentIdAndUserId(commentId, userId)?.let {
            HotDealCommentLikeModel(id = it.id, commentId = it.commentId, userId = it.userId, createdAt = it.createdAt)
        }

    override fun save(model: HotDealCommentLikeModel): HotDealCommentLikeModel {
        val entity = HotDealCommentLikeEntity(commentId = model.commentId, userId = model.userId)
        val saved = hotDealCommentLikeRepository.save(entity)
        return HotDealCommentLikeModel(id = saved.id, commentId = saved.commentId, userId = saved.userId, createdAt = saved.createdAt)
    }

    override fun delete(model: HotDealCommentLikeModel) {
        model.id?.let { hotDealCommentLikeRepository.deleteById(it) }
    }

    override fun findAllByUserIdAndCommentIds(userId: Long, commentIds: List<Long>): List<HotDealCommentLikeModel> =
        hotDealCommentLikeRepository.findAllByUserIdAndCommentIdIn(userId, commentIds).map {
            HotDealCommentLikeModel(id = it.id, commentId = it.commentId, userId = it.userId, createdAt = it.createdAt)
        }
}
