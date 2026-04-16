package dev.hddc.domains.analytics.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface LinkClickRepository : JpaRepository<LinkClickEntity, Long> {
    fun countByProfileIdAndCreatedAtAfter(profileId: Long, after: Instant): Long

    @Query(
        """
        SELECT CAST(lc.created_at AS DATE) as date, COUNT(lc) as cnt
        FROM link_click lc
        WHERE lc.profile_id = :profileId AND lc.created_at >= :after
        GROUP BY CAST(lc.created_at AS DATE)
        ORDER BY date
        """,
        nativeQuery = true,
    )
    fun countDailyByProfileId(profileId: Long, after: Instant): List<Array<Any>>

    @Query(
        """
        SELECT lc.link_id, COUNT(lc) as cnt
        FROM link_click lc
        WHERE lc.profile_id = :profileId AND lc.created_at >= :after
        GROUP BY lc.link_id
        ORDER BY cnt DESC
        LIMIT :limit
        """,
        nativeQuery = true,
    )
    fun findTopClickedLinks(profileId: Long, after: Instant, limit: Int): List<Array<Any>>
}
