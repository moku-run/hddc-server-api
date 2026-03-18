package dev.hddc.framework.upload

import dev.hddc.domains.profile.application.ports.output.FileUploadPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

@Component
class LocalFileUploadAdapter(
    @Value("\${upload.path:./uploads}")
    private val uploadPath: String,

    @Value("\${upload.base-url:http://localhost:8080/public/uploads}")
    private val baseUrl: String,
) : FileUploadPort {

    override fun upload(file: MultipartFile, directory: String): String {
        val dir = Paths.get(uploadPath, directory)
        Files.createDirectories(dir)

        val extension = file.originalFilename
            ?.substringAfterLast('.', "")
            ?.takeIf { it.isNotBlank() }
            ?: "png"

        val filename = "${UUID.randomUUID()}.$extension"
        val targetPath = dir.resolve(filename)

        file.transferTo(targetPath.toFile())

        return "$baseUrl/$directory/$filename"
    }
}
