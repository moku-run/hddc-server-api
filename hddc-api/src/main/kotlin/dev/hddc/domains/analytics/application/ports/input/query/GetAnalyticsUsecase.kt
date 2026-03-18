package dev.hddc.domains.analytics.application.ports.input.query

data class AnalyticsSummary(
    val totalViews: Long,
    val totalClicks: Long,
    val clickRate: Double,
)

data class DailyAnalytics(
    val date: String,
    val views: Long,
    val clicks: Long,
)

data class TopLink(
    val linkId: Long,
    val title: String,
    val url: String,
    val clicks: Long,
)

interface GetAnalyticsUsecase {
    fun getSummary(userId: Long, period: String): AnalyticsSummary
    fun getDaily(userId: Long, period: String): List<DailyAnalytics>
    fun getTopLinks(userId: Long, limit: Int): List<TopLink>
}
