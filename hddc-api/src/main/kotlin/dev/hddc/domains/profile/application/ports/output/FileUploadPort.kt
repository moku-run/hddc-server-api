package dev.hddc.domains.profile.application.ports.output

import java.io.InputStream

data class UploadableFile(
    val inputStream: InputStream,
    val contentType: String?,
    val size: Long,
    val originalFilename: String?,
) {
    val isEmpty: Boolean get() = size == 0L
}

data class PresignedUrlResult(
    val uploadUrl: String,
    val imageUrl: String,
    val key: String,
)

interface FileUploadPort {
    fun upload(file: UploadableFile, directory: String): String
    fun generatePresignedPutUrl(directory: String, extension: String, contentType: String): PresignedUrlResult
    fun generatePresignedGetUrl(key: String): String
    fun delete(key: String)
}
