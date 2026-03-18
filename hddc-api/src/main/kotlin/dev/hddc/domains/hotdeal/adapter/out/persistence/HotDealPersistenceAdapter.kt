package dev.hddc.domains.hotdeal.adapter.out.persistence

import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealExpiredVotePort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealLikePort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealReportPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentReportModel
import dev.hddc.domains.hotdeal.domain.model.HotDealExpiredVoteModel
import dev.hddc.domains.hotdeal.domain.model.HotDealLikeModel
import dev.hddc.domains.hotdeal.domain.model.HotDealModel
import dev.hddc.domains.hotdeal.domain.model.HotDealReportModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class HotDealPersistenceAdapter(
    private val hotDealRepository: HotDealRepository,
) : HotDealCommandPort, HotDealQueryPort {

    override fun findById(dealId: Long): HotDealModel? =
        hotDealRepository.findById(dealId).orElse(null)?.toDomain()

    override fun existsById(dealId: Long): Boolean =
        hotDealRepository.existsById(dealId)

    override fun save(model: HotDealModel): HotDealModel {
        val entity = if (model.id != null) {
            val existing = hotDealRepository.findById(model.id).orElse(null)
            if (existing != null) {
                existing.apply {
                    title = model.title
                    description = model.description
                    url = model.url
                    imageUrl = model.imageUrl
                    originalPrice = model.originalPrice
                    dealPrice = model.dealPrice
                    discountRate = model.discountRate
                    category = model.category
                    store = model.store
                    likeCount = model.likeCount
                    commentCount = model.commentCount
                    expiredVoteCount = model.expiredVoteCount
                    isExpired = model.isExpired
                    updatedAt = Instant.now()
                }
                existing
            } else {
                toNewEntity(model)
            }
        } else {
            toNewEntity(model)
        }
        return hotDealRepository.save(entity).toDomain()
    }

    override fun findActive(pageable: Pageable): Page<HotDealModel> =
        hotDealRepository.findByIsDeletedFalseAndIsExpiredFalse(pageable).map { it.toDomain() }

    override fun search(query: String, pageable: Pageable): Page<HotDealModel> =
        hotDealRepository.search(query, pageable).map { it.toDomain() }

    private fun toNewEntity(model: HotDealModel) = HotDealEntity(
        userId = model.userId,
        title = model.title,
        description = model.description,
        url = model.url,
        imageUrl = model.imageUrl,
        originalPrice = model.originalPrice,
        dealPrice = model.dealPrice,
        discountRate = model.discountRate,
        category = model.category,
        store = model.store,
        likeCount = model.likeCount,
        commentCount = model.commentCount,
        expiredVoteCount = model.expiredVoteCount,
        isExpired = model.isExpired,
    )
}

@Repository
class HotDealLikePersistenceAdapter(
    private val hotDealLikeRepository: HotDealLikeRepository,
) : HotDealLikePort {

    override fun existsByDealIdAndUserId(dealId: Long, userId: Long): Boolean =
        hotDealLikeRepository.existsByDealIdAndUserId(dealId, userId)

    override fun findByDealIdAndUserId(dealId: Long, userId: Long): HotDealLikeModel? =
        hotDealLikeRepository.findByDealIdAndUserId(dealId, userId)?.let {
            HotDealLikeModel(id = it.id, dealId = it.dealId, userId = it.userId, createdAt = it.createdAt)
        }

    override fun save(model: HotDealLikeModel): HotDealLikeModel {
        val entity = HotDealLikeEntity(dealId = model.dealId, userId = model.userId)
        val saved = hotDealLikeRepository.save(entity)
        return HotDealLikeModel(id = saved.id, dealId = saved.dealId, userId = saved.userId, createdAt = saved.createdAt)
    }

    override fun delete(model: HotDealLikeModel) {
        model.id?.let { hotDealLikeRepository.deleteById(it) }
    }

    override fun findAllByUserIdAndDealIds(userId: Long, dealIds: List<Long>): List<HotDealLikeModel> =
        hotDealLikeRepository.findAllByUserIdAndDealIdIn(userId, dealIds).map {
            HotDealLikeModel(id = it.id, dealId = it.dealId, userId = it.userId, createdAt = it.createdAt)
        }
}

@Repository
class HotDealCommentPersistenceAdapter(
    private val hotDealCommentRepository: HotDealCommentRepository,
) : HotDealCommentPort {

    override fun findById(commentId: Long): HotDealCommentModel? =
        hotDealCommentRepository.findById(commentId).orElse(null)?.toDomain()

    override fun existsById(commentId: Long): Boolean =
        hotDealCommentRepository.existsById(commentId)

    override fun save(model: HotDealCommentModel): HotDealCommentModel {
        val entity = if (model.id != null) {
            val existing = hotDealCommentRepository.findById(model.id).orElse(null)
            if (existing != null) {
                existing.apply {
                    content = model.content
                    isDeleted = model.isDeleted
                    if (model.isDeleted && deletedAt == null) deletedAt = Instant.now()
                    updatedAt = Instant.now()
                }
                existing
            } else {
                HotDealCommentEntity(
                    dealId = model.dealId, userId = model.userId,
                    parentId = model.parentId, content = model.content,
                )
            }
        } else {
            HotDealCommentEntity(
                dealId = model.dealId, userId = model.userId,
                parentId = model.parentId, content = model.content,
            )
        }
        return hotDealCommentRepository.save(entity).toDomain()
    }

    override fun findAllByDealId(dealId: Long): List<HotDealCommentModel> =
        hotDealCommentRepository.findAllByDealIdAndIsDeletedFalseOrderByCreatedAtAsc(dealId)
            .map { it.toDomain() }
}

@Repository
class HotDealExpiredVotePersistenceAdapter(
    private val hotDealExpiredVoteRepository: HotDealExpiredVoteRepository,
) : HotDealExpiredVotePort {

    override fun existsByDealIdAndUserId(dealId: Long, userId: Long): Boolean =
        hotDealExpiredVoteRepository.existsByDealIdAndUserId(dealId, userId)

    override fun findByDealIdAndUserId(dealId: Long, userId: Long): HotDealExpiredVoteModel? =
        hotDealExpiredVoteRepository.findByDealIdAndUserId(dealId, userId)?.let {
            HotDealExpiredVoteModel(id = it.id, dealId = it.dealId, userId = it.userId, createdAt = it.createdAt)
        }

    override fun findAllByUserIdAndDealIds(userId: Long, dealIds: List<Long>): List<HotDealExpiredVoteModel> =
        hotDealExpiredVoteRepository.findAllByUserIdAndDealIdIn(userId, dealIds).map {
            HotDealExpiredVoteModel(id = it.id, dealId = it.dealId, userId = it.userId, createdAt = it.createdAt)
        }

    override fun save(model: HotDealExpiredVoteModel): HotDealExpiredVoteModel {
        val entity = HotDealExpiredVoteEntity(dealId = model.dealId, userId = model.userId)
        val saved = hotDealExpiredVoteRepository.save(entity)
        return HotDealExpiredVoteModel(id = saved.id, dealId = saved.dealId, userId = saved.userId, createdAt = saved.createdAt)
    }

    override fun delete(model: HotDealExpiredVoteModel) {
        model.id?.let { hotDealExpiredVoteRepository.deleteById(it) }
    }
}

@Repository
class HotDealReportPersistenceAdapter(
    private val hotDealReportRepository: HotDealReportRepository,
    private val hotDealCommentReportRepository: HotDealCommentReportRepository,
) : HotDealReportPort {

    override fun saveDealReport(model: HotDealReportModel): HotDealReportModel {
        val entity = HotDealReportEntity(dealId = model.dealId, userId = model.userId, reason = model.reason)
        val saved = hotDealReportRepository.save(entity)
        return HotDealReportModel(id = saved.id, dealId = saved.dealId, userId = saved.userId, reason = saved.reason, createdAt = saved.createdAt)
    }

    override fun saveCommentReport(model: HotDealCommentReportModel): HotDealCommentReportModel {
        val entity = HotDealCommentReportEntity(commentId = model.commentId, userId = model.userId, reason = model.reason)
        val saved = hotDealCommentReportRepository.save(entity)
        return HotDealCommentReportModel(id = saved.id, commentId = saved.commentId, userId = saved.userId, reason = saved.reason, createdAt = saved.createdAt)
    }
}
