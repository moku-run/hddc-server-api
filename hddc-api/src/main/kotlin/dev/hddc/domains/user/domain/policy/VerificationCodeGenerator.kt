package dev.hddc.domains.user.domain.policy

import java.security.SecureRandom

object VerificationCodeGenerator {
    private const val CODE_LENGTH = 6
    private const val NUMBER_BOUND = 10
    private val SECURE_RANDOM = SecureRandom()

    fun generate(): String = buildString(CODE_LENGTH) {
        repeat(CODE_LENGTH) {
            append(SECURE_RANDOM.nextInt(NUMBER_BOUND))
        }
    }
}
