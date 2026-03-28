package dev.hddc.domains.hotdeal.adapter.out.persistence

import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealReportPort
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentReportModel
import dev.hddc.domains.hotdeal.domain.model.HotDealReportModel
import org.springframework.stereotype.Component

@Component
class HotDealReportPersistenceAdapter(
    private val hotDealReportRepository: HotDealReportRepository,
    private val hotDealCommentReportRepository: HotDealCommentReportRepository,
) : HotDealReportPort {

    override fun saveDealReport(model: HotDealReportModel): HotDealReportModel {
        val entity = HotDealReportEntity(dealId = model.dealId, userId = model.userId, reason = model.reason)
        val saved = hotDealReportRepository.save(entity)
        return HotDealReportModel(id = saved.id, dealId = saved.dealId, userId = saved.userId, reason = saved.reason, createdAt = saved.createdAt)
    }

    override fun saveCommentReport(model: HotDealCommentReportModel): HotDealCommentReportModel {
        val entity = HotDealCommentReportEntity(commentId = model.commentId, userId = model.userId, reason = model.reason)
        val saved = hotDealCommentReportRepository.save(entity)
        return HotDealCommentReportModel(id = saved.id, commentId = saved.commentId, userId = saved.userId, reason = saved.reason, createdAt = saved.createdAt)
    }
}
