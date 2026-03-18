package dev.hddc.domains.profile.adapter.`in`.web.command.request

import dev.hddc.domains.profile.application.ports.input.command.AddSocialCommand
import dev.hddc.domains.profile.application.ports.input.command.UpdateSocialCommand
import jakarta.validation.constraints.NotBlank

data class AddSocialRequest(
    @field:NotBlank(message = "플랫폼은 필수입니다.")
    val platform: String,
    @field:NotBlank(message = "URL은 필수입니다.")
    val url: String,
) {
    fun toCommand() = AddSocialCommand(platform = platform, url = url)
}

data class UpdateSocialRequest(
    @field:NotBlank(message = "URL은 필수입니다.")
    val url: String,
) {
    fun toCommand() = UpdateSocialCommand(url = url)
}
