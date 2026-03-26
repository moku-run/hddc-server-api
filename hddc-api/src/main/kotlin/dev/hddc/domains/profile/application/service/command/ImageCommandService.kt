package dev.hddc.domains.profile.application.service.command

import dev.hddc.domains.profile.application.ports.input.command.ImageCommandUsecase
import dev.hddc.domains.profile.application.ports.input.command.ImageUrlResult
import dev.hddc.domains.profile.application.ports.output.FileUploadPort
import dev.hddc.domains.profile.application.ports.output.UploadableFile
import dev.hddc.domains.profile.application.ports.output.command.ProfileCommandPort
import dev.hddc.domains.profile.application.ports.output.command.ProfileLinkCommandPort
import dev.hddc.domains.profile.application.ports.output.query.ProfileQueryPort
import dev.hddc.domains.profile.domain.model.ProfileLinkModel
import dev.hddc.domains.profile.domain.model.ProfileModel
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ImageCommandService(
    private val profileQueryPort: ProfileQueryPort,
    private val profileCommandPort: ProfileCommandPort,
    private val profileLinkCommandPort: ProfileLinkCommandPort,
    private val fileUploadPort: FileUploadPort,
) : ImageCommandUsecase {

    override fun uploadAvatar(userId: Long, file: UploadableFile): ImageUrlResult {
        validateImage(file, maxSizeMb = 2)
        val profile = findProfile(userId)
        val url = fileUploadPort.upload(file, "avatars") // 트랜잭션 밖에서 업로드
        saveProfile(profile.copy(avatarUrl = url, updatedAt = Instant.now()))
        return ImageUrlResult(url)
    }

    @Transactional
    override fun deleteAvatar(userId: Long) {
        val profile = findProfile(userId)
        profileCommandPort.save(profile.copy(avatarUrl = null, updatedAt = Instant.now()))
    }

    override fun uploadBackground(userId: Long, file: UploadableFile): ImageUrlResult {
        validateImage(file, maxSizeMb = 5)
        val profile = findProfile(userId)
        val url = fileUploadPort.upload(file, "backgrounds")
        saveProfile(profile.copy(backgroundUrl = url, updatedAt = Instant.now()))
        return ImageUrlResult(url)
    }

    @Transactional
    override fun deleteBackground(userId: Long) {
        val profile = findProfile(userId)
        profileCommandPort.save(profile.copy(backgroundUrl = null, updatedAt = Instant.now()))
    }

    override fun uploadLinkImage(userId: Long, linkId: Long, file: UploadableFile): ImageUrlResult {
        validateImage(file, maxSizeMb = 2)
        val link = findOwnedLink(userId, linkId)
        val url = fileUploadPort.upload(file, "links")
        saveLink(link.copy(imageUrl = url, updatedAt = Instant.now()))
        return ImageUrlResult(url)
    }

    @Transactional
    override fun deleteLinkImage(userId: Long, linkId: Long) {
        val link = findOwnedLink(userId, linkId)
        profileLinkCommandPort.save(link.copy(imageUrl = null, updatedAt = Instant.now()))
    }

    @Transactional(readOnly = true)
    fun findProfile(userId: Long): ProfileModel =
        profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)

    @Transactional(readOnly = true)
    fun findOwnedLink(userId: Long, linkId: Long): ProfileLinkModel {
        val profile = findProfile(userId)
        val link = profileLinkCommandPort.findById(linkId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_LINK_NOT_FOUND.code)
        require(link.profileId == profile.id && !link.isDeleted) {
            ApiResponseCode.PROFILE_LINK_NOT_FOUND.code
        }
        return link
    }

    @Transactional
    fun saveProfile(profile: ProfileModel) {
        profileCommandPort.save(profile)
    }

    @Transactional
    fun saveLink(link: ProfileLinkModel) {
        profileLinkCommandPort.save(link)
    }

    private fun validateImage(file: UploadableFile, maxSizeMb: Int) {
        val allowedTypes = setOf("image/jpeg", "image/png", "image/gif", "image/webp")
        require(!file.isEmpty) { ApiResponseCode.UPLOAD_FILE_EMPTY.code }
        require(file.size <= maxSizeMb * 1024 * 1024L) { ApiResponseCode.UPLOAD_FILE_TOO_LARGE.code }
        require(file.contentType in allowedTypes) { ApiResponseCode.UPLOAD_INVALID_TYPE.code }
    }
}
