package dev.hddc.domains.user.adapter.`in`.web.command

import dev.hddc.domains.user.adapter.`in`.web.request.SignUpRequest
import dev.hddc.domains.user.application.ports.input.command.SignUpResult
import dev.hddc.domains.user.application.ports.input.command.SignUpUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Sign Up", description = "회원가입 API")
@RestController
class SignUpApi(
    private val signUpUsecase: SignUpUsecase,
) {
    @Operation(summary = "회원가입")
    @PostMapping("/api/auth/sign-up")
    fun signUp(
        @Valid @RequestBody request: SignUpRequest,
    ): ApiResult<SignUpResult> =
        ApiResponse.of(ApiResponseCode.CREATED, signUpUsecase.execute(request.toCommand()))
}
