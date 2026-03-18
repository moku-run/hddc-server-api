package dev.hddc.domains.analytics.application.service.query

import dev.hddc.domains.analytics.application.ports.input.query.AnalyticsSummary
import dev.hddc.domains.analytics.application.ports.input.query.DailyAnalytics
import dev.hddc.domains.analytics.application.ports.input.query.GetAnalyticsUsecase
import dev.hddc.domains.analytics.application.ports.input.query.TopLink
import dev.hddc.domains.analytics.application.ports.output.query.AnalyticsQueryPort
import dev.hddc.domains.profile.application.ports.output.query.ProfileQueryPort
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class AnalyticsQueryService(
    private val profileQueryPort: ProfileQueryPort,
    private val analyticsQueryPort: AnalyticsQueryPort,
) : GetAnalyticsUsecase {

    @Transactional(readOnly = true)
    override fun getSummary(userId: Long, period: String): AnalyticsSummary {
        val profile = profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        val since = parsePeriod(period)
        val views = analyticsQueryPort.countViewsSince(profile.id!!, since)
        val clicks = analyticsQueryPort.countClicksSince(profile.id!!, since)
        val clickRate = if (views > 0) (clicks.toDouble() / views * 100) else 0.0
        return AnalyticsSummary(totalViews = views, totalClicks = clicks, clickRate = clickRate)
    }

    @Transactional(readOnly = true)
    override fun getDaily(userId: Long, period: String): List<DailyAnalytics> {
        val profile = profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        val since = parsePeriod(period)
        val dailyViews = analyticsQueryPort.dailyViewsSince(profile.id!!, since).associateBy { it.date }
        val dailyClicks = analyticsQueryPort.dailyClicksSince(profile.id!!, since).associateBy { it.date }
        val allDates = (dailyViews.keys + dailyClicks.keys).sorted()
        return allDates.map { date ->
            DailyAnalytics(
                date = date,
                views = dailyViews[date]?.count ?: 0,
                clicks = dailyClicks[date]?.count ?: 0,
            )
        }
    }

    @Transactional(readOnly = true)
    override fun getTopLinks(userId: Long, limit: Int): List<TopLink> {
        val profile = profileQueryPort.findByUserIdWithDetails(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        val since = parsePeriod("30d")
        val topStats = analyticsQueryPort.topClickedLinks(profile.id!!, since, limit)
        val linksById = profile.links.associateBy { it.id }
        return topStats.mapNotNull { stat ->
            val link = linksById[stat.linkId] ?: return@mapNotNull null
            TopLink(linkId = stat.linkId, title = link.title, url = link.url, clicks = stat.clicks)
        }
    }

    private fun parsePeriod(period: String): Instant {
        val days = when (period) {
            "7d" -> 7L
            "30d" -> 30L
            "90d" -> 90L
            else -> 7L
        }
        return Instant.now().minus(days, ChronoUnit.DAYS)
    }
}
