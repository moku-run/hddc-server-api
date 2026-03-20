package dev.hddc.domains.profile.application.service.command

import dev.hddc.domains.profile.application.ports.input.command.PresignedUploadRequest
import dev.hddc.domains.profile.application.ports.input.command.PresignedUploadResult
import dev.hddc.domains.profile.application.ports.input.command.PresignedUploadUsecase
import dev.hddc.domains.profile.application.ports.output.FileUploadPort
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service

@Service
class PresignedUploadService(
    private val fileUploadPort: FileUploadPort,
) : PresignedUploadUsecase {

    private val allowedContentTypes = setOf(
        "image/jpeg", "image/png", "image/gif", "image/webp",
    )
    private val allowedDirectories = setOf(
        "link/avatars", "link/backgrounds", "link/profiles",
    )
    private val maxExtensionLength = 10

    override fun generateUploadUrl(userId: Long, request: PresignedUploadRequest): PresignedUploadResult {
        require(request.contentType in allowedContentTypes) {
            ApiResponseCode.UPLOAD_INVALID_TYPE.code
        }
        require(request.directory in allowedDirectories) {
            ApiResponseCode.INVALID_REQUEST.code
        }
        require(request.extension.length <= maxExtensionLength) {
            ApiResponseCode.INVALID_REQUEST.code
        }

        val result = fileUploadPort.generatePresignedPutUrl(
            directory = request.directory,
            extension = request.extension,
            contentType = request.contentType,
        )

        return PresignedUploadResult(
            uploadUrl = result.uploadUrl,
            imageUrl = result.imageUrl,
            key = result.key,
        )
    }
}
