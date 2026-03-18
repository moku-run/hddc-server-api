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

interface FileUploadPort {
    fun upload(file: UploadableFile, directory: String): String
}
