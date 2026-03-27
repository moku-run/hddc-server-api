package dev.hddc.domains.user.application.service.command

import dev.hddc.domains.user.application.ports.input.command.EmailVerificationUsecase
import dev.hddc.domains.user.application.ports.output.command.EmailSendPort
import dev.hddc.domains.user.application.ports.output.command.VerificationCachePort
import dev.hddc.domains.user.application.ports.output.validation.UserValidationPort
import dev.hddc.domains.user.application.ports.output.validation.VerificationCodeValidator
import dev.hddc.domains.user.domain.policy.VerificationCodeGenerator
import dev.hddc.domains.user.domain.spec.VerificationSpec
import org.springframework.stereotype.Service

@Service
class EmailVerificationService(
    private val userValidationPort: UserValidationPort,
    private val verificationCachePort: VerificationCachePort,
    private val emailSendPort: EmailSendPort,
    private val verificationCodeValidator: VerificationCodeValidator,
) : EmailVerificationUsecase {

    override fun send(email: String) {
        userValidationPort.requireEmailNotExists(email)
        val code = VerificationCodeGenerator.generate()
        val cacheKey = VerificationSpec.signUpKey(email)
        verificationCachePort.save(cacheKey, code, VerificationSpec.codeTimeToLive())
        emailSendPort.sendVerificationCode(email, code) { verificationCachePort.delete(cacheKey) }
    }

    override fun verify(email: String, code: String) {
        val cacheKey = VerificationSpec.signUpKey(email)
        val attemptsKey = VerificationSpec.signUpAttemptsKey(email)

        verificationCodeValidator.validateCode(cacheKey, attemptsKey, code)

        verificationCachePort.save(
            cacheKey,
            VerificationSpec.COMPLETED,
            VerificationSpec.verifiedSessionTimeToLive(),
        )
    }
}
