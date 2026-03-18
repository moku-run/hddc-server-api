package dev.hddc.domains.profile.application.ports.input.command

import dev.hddc.domains.profile.application.ports.output.UploadableFile

data class ImageUrlResult(val url: String)

interface ImageCommandUsecase {
    fun uploadAvatar(userId: Long, file: UploadableFile): ImageUrlResult
    fun deleteAvatar(userId: Long)
    fun uploadBackground(userId: Long, file: UploadableFile): ImageUrlResult
    fun deleteBackground(userId: Long)
    fun uploadLinkImage(userId: Long, linkId: Long, file: UploadableFile): ImageUrlResult
    fun deleteLinkImage(userId: Long, linkId: Long)
}
