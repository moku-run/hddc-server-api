package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.HotDealCommentReportModel
import dev.hddc.domains.hotdeal.domain.model.HotDealReportModel

interface HotDealReportPort {
    fun saveDealReport(model: HotDealReportModel): HotDealReportModel
    fun saveCommentReport(model: HotDealCommentReportModel): HotDealCommentReportModel
}
