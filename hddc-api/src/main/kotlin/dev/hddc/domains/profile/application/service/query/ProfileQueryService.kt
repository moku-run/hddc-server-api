package dev.hddc.domains.profile.application.service.query

import dev.hddc.domains.profile.application.ports.input.query.GetMyProfileUsecase
import dev.hddc.domains.profile.application.ports.input.query.GetPublicProfileUsecase
import dev.hddc.domains.profile.application.ports.input.query.ValidateSlugResult
import dev.hddc.domains.profile.application.ports.input.query.GetCuratedProfilesUsecase
import dev.hddc.domains.profile.application.ports.input.query.ValidateSlugUsecase
import dev.hddc.domains.profile.application.ports.output.query.ProfileQueryPort
import dev.hddc.domains.profile.domain.model.ProfileModel
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfileQueryService(
    private val profileQueryPort: ProfileQueryPort,
) : GetMyProfileUsecase, GetPublicProfileUsecase, ValidateSlugUsecase, GetCuratedProfilesUsecase {

    @Transactional(readOnly = true)
    override fun execute(userId: Long): ProfileModel {
        return profileQueryPort.findByUserIdWithDetails(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
    }

    @Transactional(readOnly = true)
    override fun execute(slug: String): ProfileModel {
        return profileQueryPort.findBySlugWithDetails(slug)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
    }

    @Transactional(readOnly = true)
    override fun execute(userId: Long, slug: String): ValidateSlugResult {
        val profile = profileQueryPort.findByUserId(userId)
        if (profile != null && profile.slug == slug) {
            return ValidateSlugResult(available = true, slug = slug)
        }
        val exists = profileQueryPort.existsBySlug(slug)
        return ValidateSlugResult(available = !exists, slug = slug)
    }

    @Transactional(readOnly = true)
    override fun execute(limit: Int): List<ProfileModel> =
        profileQueryPort.findCuratedProfiles(limit)
}
