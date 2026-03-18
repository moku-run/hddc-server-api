package dev.hddc.domains.profile.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ProfileLinkRepository : JpaRepository<ProfileLinkEntity, Long> {
    fun findAllByProfileIdAndIsDeletedFalseOrderBySortOrderAsc(profileId: Long): List<ProfileLinkEntity>
    fun countByProfileIdAndIsDeletedFalse(profileId: Long): Long
}
