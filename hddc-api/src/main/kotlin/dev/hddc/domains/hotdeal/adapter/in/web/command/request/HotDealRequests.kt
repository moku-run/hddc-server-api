package dev.hddc.domains.hotdeal.adapter.`in`.web.command.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AddCommentRequest(
    @field:NotBlank(message = "댓글 내용은 필수입니다.")
    @field:Size(max = 1000, message = "댓글은 1000자 이하여야 합니다.")
    val content: String,
    val parentId: Long? = null,
)

data class ReportRequest(
    @field:NotBlank(message = "신고 사유는 필수입니다.")
    val reason: String,
)
