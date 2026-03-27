package dev.hddc.domains.user.adapter.out.validation

import dev.hddc.domains.user.application.ports.output.command.VerificationCachePort
import dev.hddc.domains.user.application.ports.output.validation.VerificationCodeValidator
import dev.hddc.domains.user.domain.spec.VerificationSpec
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Component

@Component
class VerificationCodeValidationAdapter(
    private val verificationCachePort: VerificationCachePort,
) : VerificationCodeValidator {

    override fun validateCode(cacheKey: String, attemptsKey: String, inputCode: String) {
        checkAttemptsNotExceeded(attemptsKey)

        val storedCode = verificationCachePort.getValue(cacheKey)
            ?: throw IllegalArgumentException(ApiResponseCode.VERIFICATION_EXPIRED.code)

        if (storedCode != inputCode) {
            val attempts = verificationCachePort.increment(attemptsKey, VerificationSpec.codeTimeToLive())
            if (attempts >= VerificationSpec.MAX_ATTEMPTS) {
                throw IllegalArgumentException(ApiResponseCode.VERIFICATION_ATTEMPTS_EXCEEDED.code)
            }
            throw IllegalArgumentException(ApiResponseCode.VERIFICATION_INVALID_CODE.code)
        }

        verificationCachePort.delete(attemptsKey)
    }

    private fun checkAttemptsNotExceeded(attemptsKey: String) {
        val attempts = verificationCachePort.getValue(attemptsKey)?.toLongOrNull() ?: 0L
        require(attempts < VerificationSpec.MAX_ATTEMPTS) {
            ApiResponseCode.VERIFICATION_ATTEMPTS_EXCEEDED.code
        }
    }
}
