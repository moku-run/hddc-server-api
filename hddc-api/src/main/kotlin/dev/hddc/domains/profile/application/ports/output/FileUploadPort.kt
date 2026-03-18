package dev.hddc.domains.profile.application.ports.output

import org.springframework.web.multipart.MultipartFile

interface FileUploadPort {
    fun upload(file: MultipartFile, directory: String): String
}
