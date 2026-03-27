package dev.hddc.domains.user.adapter.`in`.web.query

import dev.hddc.domains.user.application.ports.input.query.CheckNicknameResult
import dev.hddc.domains.user.application.ports.input.query.CheckNicknameUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Check Nickname", description = "닉네임 중복 확인 API")
@RestController
class CheckNicknameApi(
    private val checkNicknameUsecase: CheckNicknameUsecase,
) {
    @Operation(summary = "닉네임 중복 확인")
    @GetMapping("/api/auth/check-nickname")
    fun check(
        @RequestParam nickname: String,
    ): ApiResult<CheckNicknameResult> =
        ApiResponse.of(ApiResponseCode.OK, checkNicknameUsecase.execute(nickname))
}
