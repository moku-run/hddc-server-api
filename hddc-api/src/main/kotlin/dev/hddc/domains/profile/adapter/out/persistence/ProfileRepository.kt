package dev.hddc.domains.profile.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProfileRepository : JpaRepository<ProfileEntity, Long> {
    fun findByUserId(userId: Long): ProfileEntity?

    @Query("SELECT p FROM ProfileEntity p LEFT JOIN FETCH p.links LEFT JOIN FETCH p.socials WHERE p.slug = :slug")
    fun findBySlugWithLinksAndSocials(slug: String): ProfileEntity?

    @Query("SELECT p FROM ProfileEntity p LEFT JOIN FETCH p.links LEFT JOIN FETCH p.socials WHERE p.userId = :userId")
    fun findByUserIdWithLinksAndSocials(userId: Long): ProfileEntity?

    fun existsBySlug(slug: String): Boolean

    @Query("SELECT p FROM ProfileEntity p WHERE p.isDeleted = false AND p.avatarUrl IS NOT NULL ORDER BY p.createdAt DESC LIMIT :limit")
    fun findCuratedProfiles(limit: Int): List<ProfileEntity>
}
