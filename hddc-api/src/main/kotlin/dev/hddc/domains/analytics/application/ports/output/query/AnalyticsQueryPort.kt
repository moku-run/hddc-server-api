package dev.hddc.domains.analytics.application.ports.output.query

import java.time.Instant

interface AnalyticsQueryPort {
    fun countViewsSince(profileId: Long, since: Instant): Long
    fun countClicksSince(profileId: Long, since: Instant): Long
    fun dailyViewsSince(profileId: Long, since: Instant): List<DailyStat>
    fun dailyClicksSince(profileId: Long, since: Instant): List<DailyStat>
    fun topClickedLinks(profileId: Long, since: Instant, limit: Int): List<TopLinkStat>
}

data class DailyStat(val date: String, val count: Long)
data class TopLinkStat(val linkId: Long, val clicks: Long)
