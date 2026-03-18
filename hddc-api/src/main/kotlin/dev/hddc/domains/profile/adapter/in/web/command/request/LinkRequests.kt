package dev.hddc.domains.profile.adapter.`in`.web.command.request

import dev.hddc.domains.profile.application.ports.input.command.AddLinkCommand
import dev.hddc.domains.profile.application.ports.input.command.UpdateLinkCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AddLinkRequest(
    @field:NotBlank(message = "링크 제목은 필수입니다.")
    @field:Size(max = 20, message = "링크 제목은 20자 이하여야 합니다.")
    val title: String,

    @field:NotBlank(message = "링크 URL은 필수입니다.")
    val url: String,

    @field:Size(max = 40, message = "설명은 40자 이하여야 합니다.")
    val description: String? = null,
) {
    fun toCommand() = AddLinkCommand(title = title, url = url, description = description)
}

data class UpdateLinkRequest(
    @field:Size(max = 20, message = "링크 제목은 20자 이하여야 합니다.")
    val title: String? = null,
    val url: String? = null,
    @field:Size(max = 40, message = "설명은 40자 이하여야 합니다.")
    val description: String? = null,
) {
    fun toCommand() = UpdateLinkCommand(title = title, url = url, description = description)
}

data class ReorderRequest(
    val orderedIds: List<Long>,
)
