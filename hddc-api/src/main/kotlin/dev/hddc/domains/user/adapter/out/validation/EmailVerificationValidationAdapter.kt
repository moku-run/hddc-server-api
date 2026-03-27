package dev.hddc.domains.user.adapter.out.validation

import dev.hddc.domains.user.application.ports.output.command.VerificationCachePort
import dev.hddc.domains.user.application.ports.output.validation.EmailVerificationValidationPort
import dev.hddc.domains.user.domain.spec.VerificationSpec
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Component

@Component
class EmailVerificationValidationAdapter(
    private val verificationCachePort: VerificationCachePort,
) : EmailVerificationValidationPort {

    override fun requireEmailVerified(email: String) {
        val cacheKey = VerificationSpec.signUpKey(email)
        val value = verificationCachePort.getValue(cacheKey)
        require(value == VerificationSpec.COMPLETED) {
            ApiResponseCode.VERIFICATION_REQUIRED.code
        }
    }
}
