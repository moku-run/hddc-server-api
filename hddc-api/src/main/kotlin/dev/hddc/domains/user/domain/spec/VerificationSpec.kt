package dev.hddc.domains.user.domain.spec

import java.time.Duration

object VerificationSpec {
    private const val SIGN_UP_KEY_PREFIX = "VERIFICATION:SIGN_UP:"
    private const val SIGN_UP_ATTEMPTS_PREFIX = "VERIFICATION:SIGN_UP_ATTEMPTS:"
    private const val RESET_PASSWORD_KEY_PREFIX = "VERIFICATION:RESET_PASSWORD:"
    private const val RESET_PASSWORD_ATTEMPTS_PREFIX = "VERIFICATION:RESET_PASSWORD_ATTEMPTS:"

    const val COMPLETED = "VERIFICATION_COMPLETED"
    const val RESET_PASSWORD_COMPLETED = "VERIFICATION_COMPLETED_RESET_PASSWORD"
    const val MAX_ATTEMPTS = 5

    fun codeTimeToLive(): Duration = Duration.ofMinutes(5L)
    fun verifiedSessionTimeToLive(): Duration = Duration.ofMinutes(15L)

    fun signUpKey(email: String): String = "$SIGN_UP_KEY_PREFIX$email"
    fun signUpAttemptsKey(email: String): String = "$SIGN_UP_ATTEMPTS_PREFIX$email"

    fun resetPasswordKey(email: String): String = "$RESET_PASSWORD_KEY_PREFIX$email"
    fun resetPasswordAttemptsKey(email: String): String = "$RESET_PASSWORD_ATTEMPTS_PREFIX$email"
}
