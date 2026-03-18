package dev.hddc.domains.analytics.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface PageViewRepository : JpaRepository<PageViewEntity, Long> {
    fun countByProfileIdAndCreatedAtAfter(profileId: Long, after: Instant): Long

    @Query(
        """
        SELECT CAST(pv.created_at AS DATE) as date, COUNT(pv) as cnt
        FROM his_page_view pv
        WHERE pv.profile_id = :profileId AND pv.created_at >= :after
        GROUP BY CAST(pv.created_at AS DATE)
        ORDER BY date
        """,
        nativeQuery = true,
    )
    fun countDailyByProfileId(profileId: Long, after: Instant): List<Array<Any>>
}
