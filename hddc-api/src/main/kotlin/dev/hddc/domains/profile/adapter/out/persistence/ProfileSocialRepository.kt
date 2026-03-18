package dev.hddc.domains.profile.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ProfileSocialRepository : JpaRepository<ProfileSocialEntity, Long> {
    fun findAllByProfileIdAndIsDeletedFalseOrderBySortOrderAsc(profileId: Long): List<ProfileSocialEntity>
    fun countByProfileIdAndIsDeletedFalse(profileId: Long): Long
    fun existsByProfileIdAndPlatformAndIsDeletedFalse(profileId: Long, platform: String): Boolean
}
