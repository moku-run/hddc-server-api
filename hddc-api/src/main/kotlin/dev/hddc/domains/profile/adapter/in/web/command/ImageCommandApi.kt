package dev.hddc.domains.profile.adapter.`in`.web.command

import dev.hddc.domains.profile.application.ports.input.command.ImageCommandUsecase
import dev.hddc.domains.profile.application.ports.input.command.ImageUrlResult
import dev.hddc.domains.profile.application.ports.output.UploadableFile
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Profile Image Command", description = "프로필 이미지 업로드/삭제 API")
@RestController
class ImageCommandApi(
    private val imageCommandUsecase: ImageCommandUsecase,
) {
    @Operation(summary = "아바타 업로드")
    @PostMapping("/api/profiles/me/avatar", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadAvatar(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @RequestParam("file") file: MultipartFile,
    ): ApiResult<ImageUrlResult> =
        ApiResponse.of(ApiResponseCode.OK, imageCommandUsecase.uploadAvatar(user.userId, file.toUploadable()))

    @Operation(summary = "아바타 삭제")
    @DeleteMapping("/api/profiles/me/avatar")
    fun deleteAvatar(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
    ): ApiResult<Nothing> {
        imageCommandUsecase.deleteAvatar(user.userId)
        return ApiResponse.of(ApiResponseCode.DELETED)
    }

    @Operation(summary = "배경 이미지 업로드")
    @PostMapping("/api/profiles/me/background", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadBackground(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @RequestParam("file") file: MultipartFile,
    ): ApiResult<ImageUrlResult> =
        ApiResponse.of(ApiResponseCode.OK, imageCommandUsecase.uploadBackground(user.userId, file.toUploadable()))

    @Operation(summary = "배경 이미지 삭제")
    @DeleteMapping("/api/profiles/me/background")
    fun deleteBackground(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
    ): ApiResult<Nothing> {
        imageCommandUsecase.deleteBackground(user.userId)
        return ApiResponse.of(ApiResponseCode.DELETED)
    }

    @Operation(summary = "링크 이미지 업로드")
    @PostMapping("/api/profiles/me/links/{linkId}/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadLinkImage(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable linkId: Long,
        @RequestParam("file") file: MultipartFile,
    ): ApiResult<ImageUrlResult> =
        ApiResponse.of(ApiResponseCode.OK, imageCommandUsecase.uploadLinkImage(user.userId, linkId, file.toUploadable()))

    @Operation(summary = "링크 이미지 삭제")
    @DeleteMapping("/api/profiles/me/links/{linkId}/image")
    fun deleteLinkImage(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable linkId: Long,
    ): ApiResult<Nothing> {
        imageCommandUsecase.deleteLinkImage(user.userId, linkId)
        return ApiResponse.of(ApiResponseCode.DELETED)
    }

    private fun MultipartFile.toUploadable() = UploadableFile(
        inputStream = inputStream,
        contentType = contentType,
        size = size,
        originalFilename = originalFilename,
    )
}
