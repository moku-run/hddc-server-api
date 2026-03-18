package dev.hddc.domains.profile.application.ports.input.command

import org.springframework.web.multipart.MultipartFile

data class ImageUrlResult(val url: String)

interface ImageCommandUsecase {
    fun uploadAvatar(userId: Long, file: MultipartFile): ImageUrlResult
    fun deleteAvatar(userId: Long)
    fun uploadBackground(userId: Long, file: MultipartFile): ImageUrlResult
    fun deleteBackground(userId: Long)
    fun uploadLinkImage(userId: Long, linkId: Long, file: MultipartFile): ImageUrlResult
    fun deleteLinkImage(userId: Long, linkId: Long)
}
