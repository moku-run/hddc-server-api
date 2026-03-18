package dev.hddc.domains.analytics.application.ports.output.command

interface AnalyticsCommandPort {
    fun recordPageView(profileId: Long, slug: String, ip: String?, userAgent: String?, referer: String?)
    fun recordLinkClick(profileId: Long, linkId: Long, slug: String, ip: String?, userAgent: String?, referer: String?)
}
