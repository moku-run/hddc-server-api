package dev.hddc.domains.hotdeal.adapter.out.command

import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.HotDealCommentReportEntity
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.HotDealReportEntity
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.HotDealCommentReportRepository
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.HotDealReportRepository
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealReportPort
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentReportModel
import dev.hddc.domains.hotdeal.domain.model.HotDealReportModel
import org.springframework.stereotype.Component

@Component
class HotDealReportCommandAdapter(
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
