package dev.hddc.domains.profile.application.ports.input.query

data class ValidateSlugResult(
    val available: Boolean,
    val slug: String,
)

interface ValidateSlugUsecase {
    fun execute(userId: Long, slug: String): ValidateSlugResult
}
