package dev.hddc.domains.profile.application.ports.output.query

import dev.hddc.domains.profile.domain.model.ProfileModel

interface ProfileQueryPort {
    fun findByUserId(userId: Long): ProfileModel?
    fun findByUserIdWithDetails(userId: Long): ProfileModel?
    fun findBySlugWithDetails(slug: String): ProfileModel?
    fun existsBySlug(slug: String): Boolean
    fun findCuratedProfiles(limit: Int): List<ProfileModel>
}
