package dev.hddc.domains.hotdeal.adapter.out.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface HotDealRepository : JpaRepository<HotDealEntity, Long> {
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
}
