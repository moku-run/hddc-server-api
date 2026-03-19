package dev.hddc.domains.profile.application.ports.input.command

data class PresignedUploadRequest(
    val directory: String,
    val extension: String,
    val contentType: String,
)

data class PresignedUploadResult(
    val uploadUrl: String,
    val imageUrl: String,
    val key: String,
)

interface PresignedUploadUsecase {
    fun generateUploadUrl(userId: Long, request: PresignedUploadRequest): PresignedUploadResult
}
