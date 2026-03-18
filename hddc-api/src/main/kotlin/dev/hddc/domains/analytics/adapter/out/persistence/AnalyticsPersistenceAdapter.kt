package dev.hddc.domains.analytics.adapter.out.persistence

import dev.hddc.domains.analytics.application.ports.output.command.AnalyticsCommandPort
import dev.hddc.domains.analytics.application.ports.output.query.AnalyticsQueryPort
import dev.hddc.domains.analytics.application.ports.output.query.DailyStat
import dev.hddc.domains.analytics.application.ports.output.query.TopLinkStat
import org.springframework.stereotype.Repository
import java.sql.Date
import java.time.Instant

@Repository
class AnalyticsPersistenceAdapter(
    private val pageViewRepository: PageViewRepository,
    private val linkClickRepository: LinkClickRepository,
) : AnalyticsCommandPort, AnalyticsQueryPort {

    override fun recordPageView(profileId: Long, slug: String, ip: String?, userAgent: String?, referer: String?) {
        pageViewRepository.save(
            PageViewEntity(profileId = profileId, slug = slug, ip = ip, userAgent = userAgent, referer = referer)
        )
    }

    override fun recordLinkClick(profileId: Long, linkId: Long, slug: String, ip: String?, userAgent: String?, referer: String?) {
        linkClickRepository.save(
            LinkClickEntity(profileId = profileId, linkId = linkId, slug = slug, ip = ip, userAgent = userAgent, referer = referer)
        )
    }

    override fun countViewsSince(profileId: Long, since: Instant): Long =
        pageViewRepository.countByProfileIdAndCreatedAtAfter(profileId, since)

    override fun countClicksSince(profileId: Long, since: Instant): Long =
        linkClickRepository.countByProfileIdAndCreatedAtAfter(profileId, since)

    override fun dailyViewsSince(profileId: Long, since: Instant): List<DailyStat> =
        pageViewRepository.countDailyByProfileId(profileId, since).map { row ->
            DailyStat(date = (row[0] as Date).toString(), count = (row[1] as Number).toLong())
        }

    override fun dailyClicksSince(profileId: Long, since: Instant): List<DailyStat> =
        linkClickRepository.countDailyByProfileId(profileId, since).map { row ->
            DailyStat(date = (row[0] as Date).toString(), count = (row[1] as Number).toLong())
        }

    override fun topClickedLinks(profileId: Long, since: Instant, limit: Int): List<TopLinkStat> =
        linkClickRepository.findTopClickedLinks(profileId, since, limit).map { row ->
            TopLinkStat(linkId = (row[0] as Number).toLong(), clicks = (row[1] as Number).toLong())
        }
}
