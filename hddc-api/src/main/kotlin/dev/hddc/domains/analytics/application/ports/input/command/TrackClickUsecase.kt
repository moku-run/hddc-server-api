package dev.hddc.domains.analytics.application.ports.input.command

interface TrackClickUsecase {
    fun trackClick(slug: String, linkId: Long, ip: String?, userAgent: String?, referer: String?)
    fun trackPageView(slug: String, ip: String?, userAgent: String?, referer: String?)
}
