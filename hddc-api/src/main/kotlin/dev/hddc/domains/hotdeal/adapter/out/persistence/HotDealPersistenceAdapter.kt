package dev.hddc.domains.hotdeal.adapter.out.persistence

import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentLikePort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealExpiredVotePort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealLikePort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealReportPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealPageData
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import org.springframework.data.domain.Sort
import dev.hddc.domains.hotdeal.domain.model.CreateHotDealCommentModel
import dev.hddc.domains.hotdeal.domain.model.CreateHotDealModel
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentLikeModel
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentReportModel
import dev.hddc.domains.hotdeal.domain.model.HotDealExpiredVoteModel
import dev.hddc.domains.hotdeal.domain.model.HotDealLikeModel
import dev.hddc.domains.hotdeal.domain.model.HotDealModel
import dev.hddc.domains.hotdeal.domain.model.HotDealReportModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.time.Instant

@Component
class HotDealPersistenceAdapter(
    private val hotDealRepository: HotDealRepository,
) : HotDealCommandPort {

    override fun create(model: CreateHotDealModel): HotDealModel {
        val entity = HotDealEntity(
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
        )
        return hotDealRepository.save(entity).toDomain()
    }

    override fun findById(dealId: Long): HotDealModel? =
        hotDealRepository.findById(dealId).orElse(null)?.toDomain()

    override fun loadById(dealId: Long): HotDealModel =
        hotDealRepository.loadById(dealId).toDomain()

    override fun updateLikeCount(dealId: Long, count: Int) {
        val entity = hotDealRepository.loadById(dealId)
        entity.likeCount = count
        hotDealRepository.save(entity)
    }

    override fun updateCommentCount(dealId: Long, count: Int) {
        val entity = hotDealRepository.loadById(dealId)
        entity.commentCount = count
        hotDealRepository.save(entity)
    }

    override fun updateClickCount(dealId: Long, count: Int) {
        val entity = hotDealRepository.loadById(dealId)
        entity.clickCount = count
        hotDealRepository.save(entity)
    }

    override fun updateExpiredVote(dealId: Long, count: Int, expired: Boolean) {
        val entity = hotDealRepository.loadById(dealId)
        entity.expiredVoteCount = count
        entity.isExpired = expired
        hotDealRepository.save(entity)
    }

    override fun softDelete(dealId: Long) {
        val entity = hotDealRepository.loadById(dealId)
        entity.isDeleted = true
        entity.deletedAt = Instant.now()
        hotDealRepository.save(entity)
    }

    override fun update(dealId: Long, updater: (HotDealModel) -> HotDealModel): HotDealModel {
        val entity = hotDealRepository.loadById(dealId)
        val current = entity.toDomain()
        val updated = updater(current)
        entity.apply {
            title = updated.title
            description = updated.description
            url = updated.url
            imageUrl = updated.imageUrl
            originalPrice = updated.originalPrice
            dealPrice = updated.dealPrice
            discountRate = updated.discountRate
            category = updated.category
            store = updated.store
        }
        return hotDealRepository.save(entity).toDomain()
    }
}

@Component
class HotDealQueryAdapter(
    private val hotDealRepository: HotDealRepository,
) : HotDealQueryPort {

    override fun findActive(sort: String, page: Int, size: Int): HotDealPageData {
        val pageable = PageRequest.of(page, size, resolveSort(sort))
        return hotDealRepository.findByIsDeletedFalseAndIsExpiredFalse(pageable).toPageData()
    }

    override fun search(query: String, page: Int, size: Int): HotDealPageData {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        return hotDealRepository.search(query, pageable).toPageData()
    }

    override fun findAll(page: Int, size: Int): HotDealPageData {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        return hotDealRepository.findAll(pageable).toPageData()
    }

    private fun Page<HotDealEntity>.toPageData() = HotDealPageData(
        content = content.map { it.toDomain() },
        page = number,
        size = size,
        totalElements = totalElements,
        totalPages = totalPages,
    )

    private fun resolveSort(sort: String): Sort = when (sort) {
        "popular" -> Sort.by(Sort.Direction.DESC, "likeCount").and(Sort.by(Sort.Direction.DESC, "createdAt"))
        "discount" -> Sort.by(Sort.Direction.DESC, "discountRate").and(Sort.by(Sort.Direction.DESC, "createdAt"))
        else -> Sort.by(Sort.Direction.DESC, "createdAt")
    }
}

@Component
class HotDealCommentPersistenceAdapter(
    private val hotDealCommentRepository: HotDealCommentRepository,
) : HotDealCommentPort {

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
        entity.isDeleted = true
        entity.deletedAt = Instant.now()
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

@Repository
class HotDealCommentLikePersistenceAdapter(
    private val hotDealCommentLikeRepository: HotDealCommentLikeRepository,
) : HotDealCommentLikePort {

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
