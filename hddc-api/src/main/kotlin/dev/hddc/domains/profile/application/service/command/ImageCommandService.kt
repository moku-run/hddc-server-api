package dev.hddc.domains.profile.application.service.command

import dev.hddc.domains.profile.application.ports.input.command.ImageCommandUsecase
import dev.hddc.domains.profile.application.ports.input.command.ImageUrlResult
import dev.hddc.domains.profile.application.ports.output.FileUploadPort
import dev.hddc.domains.profile.application.ports.output.command.ProfileCommandPort
import dev.hddc.domains.profile.application.ports.output.command.ProfileLinkCommandPort
import dev.hddc.domains.profile.application.ports.output.query.ProfileQueryPort
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.Instant

@Service
class ImageCommandService(
    private val profileQueryPort: ProfileQueryPort,
    private val profileCommandPort: ProfileCommandPort,
    private val profileLinkCommandPort: ProfileLinkCommandPort,
    private val fileUploadPort: FileUploadPort,
) : ImageCommandUsecase {

    @Transactional
    override fun uploadAvatar(userId: Long, file: MultipartFile): ImageUrlResult {
        validateImage(file, maxSizeMb = 2)
        val profile = profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        val url = fileUploadPort.upload(file, "avatars")
        profileCommandPort.save(profile.copy(avatarUrl = url, updatedAt = Instant.now()))
        return ImageUrlResult(url)
    }

    @Transactional
    override fun deleteAvatar(userId: Long) {
        val profile = profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        profileCommandPort.save(profile.copy(avatarUrl = null, updatedAt = Instant.now()))
    }

    @Transactional
    override fun uploadBackground(userId: Long, file: MultipartFile): ImageUrlResult {
        validateImage(file, maxSizeMb = 5)
        val profile = profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        val url = fileUploadPort.upload(file, "backgrounds")
        profileCommandPort.save(profile.copy(backgroundUrl = url, updatedAt = Instant.now()))
        return ImageUrlResult(url)
    }

    @Transactional
    override fun deleteBackground(userId: Long) {
        val profile = profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        profileCommandPort.save(profile.copy(backgroundUrl = null, updatedAt = Instant.now()))
    }

    @Transactional
    override fun uploadLinkImage(userId: Long, linkId: Long, file: MultipartFile): ImageUrlResult {
        validateImage(file, maxSizeMb = 2)
        val profile = profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        val link = profileLinkCommandPort.findById(linkId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_LINK_NOT_FOUND.code)
        require(link.profileId == profile.id && !link.isDeleted) {
            ApiResponseCode.PROFILE_LINK_NOT_FOUND.code
        }
        val url = fileUploadPort.upload(file, "links")
        profileLinkCommandPort.save(link.copy(imageUrl = url, updatedAt = Instant.now()))
        return ImageUrlResult(url)
    }

    @Transactional
    override fun deleteLinkImage(userId: Long, linkId: Long) {
        val profile = profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        val link = profileLinkCommandPort.findById(linkId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_LINK_NOT_FOUND.code)
        require(link.profileId == profile.id && !link.isDeleted) {
            ApiResponseCode.PROFILE_LINK_NOT_FOUND.code
        }
        profileLinkCommandPort.save(link.copy(imageUrl = null, updatedAt = Instant.now()))
    }

    private fun validateImage(file: MultipartFile, maxSizeMb: Int) {
        val allowedTypes = setOf("image/jpeg", "image/png", "image/gif", "image/webp")
        require(!file.isEmpty) { ApiResponseCode.UPLOAD_FILE_EMPTY.code }
        require(file.size <= maxSizeMb * 1024 * 1024L) { ApiResponseCode.UPLOAD_FILE_TOO_LARGE.code }
        require(file.contentType in allowedTypes) { ApiResponseCode.UPLOAD_INVALID_TYPE.code }
    }
}
