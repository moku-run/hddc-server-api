package dev.hddc.domains.user.application.ports.input.query

data class CheckNicknameResult(
    val available: Boolean,
    val nickname: String,
)

interface CheckNicknameUsecase {
    fun execute(nickname: String): CheckNicknameResult
}
