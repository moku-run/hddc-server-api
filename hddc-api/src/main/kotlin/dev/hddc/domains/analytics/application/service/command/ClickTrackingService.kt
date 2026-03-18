package dev.hddc.domains.analytics.application.service.command

import dev.hddc.domains.analytics.application.ports.input.command.TrackClickUsecase
import dev.hddc.domains.analytics.application.ports.output.command.AnalyticsCommandPort
import dev.hddc.domains.profile.application.ports.output.query.ProfileQueryPort
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClickTrackingService(
    private val profileQueryPort: ProfileQueryPort,
    private val analyticsCommandPort: AnalyticsCommandPort,
) : TrackClickUsecase {

    @Transactional
    override fun trackClick(slug: String, linkId: Long, ip: String?, userAgent: String?, referer: String?) {
        val profile = profileQueryPort.findBySlugWithDetails(slug)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        analyticsCommandPort.recordLinkClick(profile.id!!, linkId, slug, ip, userAgent, referer)
    }

    @Transactional
    override fun trackPageView(slug: String, ip: String?, userAgent: String?, referer: String?) {
        val profile = profileQueryPort.findBySlugWithDetails(slug)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        analyticsCommandPort.recordPageView(profile.id!!, slug, ip, userAgent, referer)
    }
}
