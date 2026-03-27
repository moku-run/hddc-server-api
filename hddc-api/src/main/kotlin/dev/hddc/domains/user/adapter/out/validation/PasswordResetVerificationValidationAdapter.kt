package dev.hddc.domains.user.adapter.out.validation

import dev.hddc.domains.user.application.ports.output.command.VerificationCachePort
import dev.hddc.domains.user.application.ports.output.validation.PasswordResetVerificationValidator
import dev.hddc.domains.user.domain.spec.VerificationSpec
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Component

@Component
class PasswordResetVerificationValidationAdapter(
    private val verificationCachePort: VerificationCachePort,
) : PasswordResetVerificationValidator {

    override fun requireVerified(email: String) {
        val cacheKey = VerificationSpec.resetPasswordKey(email)
        val value = verificationCachePort.getValue(cacheKey)
        require(value == VerificationSpec.RESET_PASSWORD_COMPLETED) {
            ApiResponseCode.VERIFICATION_REQUIRED.code
        }
    }
}
