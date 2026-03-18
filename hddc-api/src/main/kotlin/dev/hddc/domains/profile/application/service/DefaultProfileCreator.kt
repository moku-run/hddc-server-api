package dev.hddc.domains.profile.application.service

import dev.hddc.domains.profile.application.ports.output.command.ProfileCommandPort
import dev.hddc.domains.profile.application.ports.output.query.ProfileQueryPort
import dev.hddc.domains.profile.domain.model.ProfileModel
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DefaultProfileCreator(
    private val profileQueryPort: ProfileQueryPort,
    private val profileCommandPort: ProfileCommandPort,
) : dev.hddc.domains.profile.application.ports.output.command.CreateDefaultProfilePort {

    override fun createDefaultProfile(userId: Long, nickname: String) {
        val slug = generateUniqueSlug(nickname)
        val model = ProfileModel(
            userId = userId,
            slug = slug,
            nickname = nickname,
        )
        profileCommandPort.save(model)
    }

    private fun generateUniqueSlug(nickname: String): String {
        val base = nickname.lowercase()
            .replace(Regex("[^a-z0-9]"), "")
            .take(20)
            .ifBlank { "user" }

        if (!profileQueryPort.existsBySlug(base)) return base

        val suffix = UUID.randomUUID().toString().take(6)
        return "${base}-$suffix"
    }
}
