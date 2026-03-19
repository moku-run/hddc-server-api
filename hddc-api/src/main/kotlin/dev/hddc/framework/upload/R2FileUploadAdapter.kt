package dev.hddc.framework.upload

import dev.hddc.domains.profile.application.ports.output.FileUploadPort
import dev.hddc.domains.profile.application.ports.output.PresignedUrlResult
import dev.hddc.domains.profile.application.ports.output.UploadableFile
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.util.UUID

@Primary
@Component
class R2FileUploadAdapter(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    private val r2Properties: R2Properties,
) : FileUploadPort {

    override fun upload(file: UploadableFile, directory: String): String {
        val extension = file.originalFilename
            ?.substringAfterLast('.', "")
            ?.takeIf { it.isNotBlank() }
            ?: "png"

        val key = "$directory/${UUID.randomUUID()}.$extension"

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(r2Properties.bucketName)
                .key(key)
                .contentType(file.contentType ?: "application/octet-stream")
                .build(),
            RequestBody.fromInputStream(file.inputStream, file.size),
        )

        return buildPublicUrl(key)
    }

    override fun generatePresignedPutUrl(directory: String, extension: String, contentType: String): PresignedUrlResult {
        val key = "$directory/${UUID.randomUUID()}.$extension"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(r2Properties.bucketName)
            .key(key)
            .contentType(contentType)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(r2Properties.putExpiredTTL)
            .putObjectRequest(putObjectRequest)
            .build()

        val presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString()

        return PresignedUrlResult(
            uploadUrl = presignedUrl,
            imageUrl = buildPublicUrl(key),
            key = key,
        )
    }

    override fun delete(key: String) {
        s3Client.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(r2Properties.bucketName)
                .key(key)
                .build()
        )
    }

    private fun buildPublicUrl(key: String): String =
        "${r2Properties.endpoint}/${r2Properties.bucketName}/$key"
}
