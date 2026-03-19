package dev.hddc.domains.profile.adapter.`in`.web.command

import dev.hddc.domains.profile.application.ports.input.command.PresignedUploadRequest
import dev.hddc.domains.profile.application.ports.input.command.PresignedUploadResult
import dev.hddc.domains.profile.application.ports.input.command.PresignedUploadUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Upload", description = "파일 업로드 API")
@RestController
class UploadApi(
    private val presignedUploadUsecase: PresignedUploadUsecase,
) {
    @Operation(summary = "이미지 업로드용 pre-signed URL 발급")
    @PostMapping("/api/upload/presigned-url")
    fun getPresignedUrl(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @RequestBody request: PresignedUploadRequest,
    ): ResponseEntity<ApiResponse<PresignedUploadResult>> =
        ApiResponse.of(ApiResponseCode.OK, presignedUploadUsecase.generateUploadUrl(user.userId, request))
}
