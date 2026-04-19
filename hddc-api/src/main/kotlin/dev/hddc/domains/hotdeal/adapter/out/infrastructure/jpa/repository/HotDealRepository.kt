package dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository

import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.HotDealClickEntity
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.HotDealCommentEntity
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.HotDealCommentLikeEntity
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.HotDealCommentReportEntity
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.HotDealEntity
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.HotDealExpiredVoteEntity
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.HotDealLikeEntity
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.HotDealReportEntity
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.BusinessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

fun HotDealRepository.loadById(id: Long): HotDealEntity =
    findById(id).orElseThrow { BusinessException(ApiResponseCode.HOT_DEAL_NOT_FOUND) }

fun HotDealCommentRepository.loadById(id: Long): HotDealCommentEntity =
    findById(id).orElseThrow { BusinessException(ApiResponseCode.HOT_DEAL_COMMENT_NOT_FOUND) }

interface HotDealRepository : JpaRepository<HotDealEntity, Long> {
    fun existsByUrlAndIsDeletedFalse(url: String): Boolean
    fun findByIsDeletedFalseAndIsExpiredFalse(pageable: Pageable): Page<HotDealEntity>

    @Query(
        """
        SELECT d FROM HotDealEntity d
        WHERE d.isDeleted = false AND d.isExpired = false
          AND (LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(d.store) LIKE LOWER(CONCAT('%', :query, '%')))
        """
    )
    fun search(query: String, pageable: Pageable): Page<HotDealEntity>
}

interface HotDealLikeRepository : JpaRepository<HotDealLikeEntity, Long> {
    fun existsByDealIdAndUserId(dealId: Long, userId: Long): Boolean
    fun findByDealIdAndUserId(dealId: Long, userId: Long): HotDealLikeEntity?
    fun findAllByUserIdAndDealIdIn(userId: Long, dealIds: List<Long>): List<HotDealLikeEntity>
}

interface HotDealCommentRepository : JpaRepository<HotDealCommentEntity, Long> {
    fun findAllByDealIdAndIsDeletedFalseOrderByCreatedAtAsc(dealId: Long): List<HotDealCommentEntity>
    fun findAllByDealIdOrderByCreatedAtAsc(dealId: Long): List<HotDealCommentEntity>

    @Query(
        """
        SELECT c FROM HotDealCommentEntity c
        WHERE c.dealId = :dealId AND c.parentId IS NULL
        ORDER BY c.id ASC
        """
    )
    fun findRootComments(dealId: Long, pageable: Pageable): List<HotDealCommentEntity>

    @Query(
        """
        SELECT c FROM HotDealCommentEntity c
        WHERE c.dealId = :dealId AND c.parentId IS NULL AND c.id > :afterId
        ORDER BY c.id ASC
        """
    )
    fun findRootCommentsAfter(dealId: Long, afterId: Long, pageable: Pageable): List<HotDealCommentEntity>

    fun findAllByParentIdInOrderByCreatedAtAsc(parentIds: List<Long>): List<HotDealCommentEntity>
}

interface HotDealExpiredVoteRepository : JpaRepository<HotDealExpiredVoteEntity, Long> {
    fun existsByDealIdAndUserId(dealId: Long, userId: Long): Boolean
    fun findByDealIdAndUserId(dealId: Long, userId: Long): HotDealExpiredVoteEntity?
    fun findAllByUserIdAndDealIdIn(userId: Long, dealIds: List<Long>): List<HotDealExpiredVoteEntity>
}

interface HotDealReportRepository : JpaRepository<HotDealReportEntity, Long>

interface HotDealCommentReportRepository : JpaRepository<HotDealCommentReportEntity, Long>

interface HotDealCommentLikeRepository : JpaRepository<HotDealCommentLikeEntity, Long> {
    fun existsByCommentIdAndUserId(commentId: Long, userId: Long): Boolean
    fun findByCommentIdAndUserId(commentId: Long, userId: Long): HotDealCommentLikeEntity?
    fun findAllByUserIdAndCommentIdIn(userId: Long, commentIds: List<Long>): List<HotDealCommentLikeEntity>
}

interface HotDealClickRepository : JpaRepository<HotDealClickEntity, Long> {
    fun existsByDealIdAndUserIdAndCreatedAtAfter(dealId: Long, userId: Long, after: java.time.Instant): Boolean
    fun existsByDealIdAndIpAndUserIdIsNullAndCreatedAtAfter(dealId: Long, ip: String, after: java.time.Instant): Boolean
    fun findAllByUserIdAndDealIdIn(userId: Long, dealIds: List<Long>): List<HotDealClickEntity>
    fun existsByDealIdAndUserId(dealId: Long, userId: Long): Boolean
}
