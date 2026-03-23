package dev.hddc.domains.profile.application.service.command

import dev.hddc.domains.profile.application.ports.input.command.ResetProfileUsecase
import dev.hddc.domains.profile.application.ports.output.command.ProfileCommandPort
import dev.hddc.domains.profile.application.ports.output.query.ProfileQueryPort
import dev.hddc.domains.profile.domain.model.ProfileModel
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ResetProfileService(
    private val profileQueryPort: ProfileQueryPort,
    private val profileCommandPort: ProfileCommandPort,
) : ResetProfileUsecase {

    @Transactional
    override fun execute(userId: Long): ProfileModel {
        val profile = profileQueryPort.findByUserIdWithDetails(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)

        val now = Instant.now()
        val reset = profile.copy(
            bio = null,
            avatarUrl = null,
            backgroundUrl = null,
            backgroundColor = null,
            backgroundTexture = null,
            linkLayout = "list",
            linkStyle = "fill",
            fontFamily = "pretendard",
            headerLayout = "center",
            linkAnimation = "none",
            colorTheme = "default",
            customPrimaryColor = null,
            customSecondaryColor = null,
            fontColor = null,
            linkRound = "sm",
            decorator1Type = null,
            decorator1Text = null,
            decorator2Type = null,
            decorator2Text = null,
            linkGradientFrom = null,
            linkGradientTo = null,
            linkBorderColor = null,
            linkBorderThick = "thin",
            pageLayout = "list",
            darkMode = false,
            updatedAt = now,
            links = profile.links.map { it.copy(isDeleted = true) },
            socials = profile.socials.map { it.copy(isDeleted = true) },
        )

        return profileCommandPort.save(reset)
    }
}
