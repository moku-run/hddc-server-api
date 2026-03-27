package dev.hddc.domains.user.application.service.command

import dev.hddc.domains.user.application.ports.input.command.PasswordResetCommand
import dev.hddc.domains.user.application.ports.input.command.PasswordResetUsecase
import dev.hddc.domains.user.application.ports.output.command.EmailSendPort
import dev.hddc.domains.user.application.ports.output.command.UserCommandPort
import dev.hddc.domains.user.application.ports.output.command.VerificationCachePort
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import dev.hddc.domains.user.application.ports.output.security.PasswordEncodePort
import dev.hddc.domains.user.application.ports.output.validation.PasswordResetVerificationValidator
import dev.hddc.domains.user.application.ports.output.validation.PasswordValidator
import dev.hddc.domains.user.application.ports.output.validation.UserValidationPort
import dev.hddc.domains.user.application.ports.output.validation.VerificationCodeValidator
import dev.hddc.domains.user.domain.policy.VerificationCodeGenerator
import dev.hddc.domains.user.domain.spec.VerificationSpec
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PasswordResetService(
    private val userQueryPort: UserQueryPort,
    private val userCommandPort: UserCommandPort,
    private val verificationCachePort: VerificationCachePort,
    private val emailSendPort: EmailSendPort,
    private val passwordEncodePort: PasswordEncodePort,
    private val passwordValidator: PasswordValidator,
    private val passwordResetVerificationValidator: PasswordResetVerificationValidator,
    private val userValidationPort: UserValidationPort,
    private val verificationCodeValidator: VerificationCodeValidator,
) : PasswordResetUsecase {

    override fun sendCode(email: String) {
        userValidationPort.requireUserExistsByEmail(email)
        val code = VerificationCodeGenerator.generate()
        val cacheKey = VerificationSpec.resetPasswordKey(email)
        verificationCachePort.save(cacheKey, code, VerificationSpec.codeTimeToLive())
        verificationCachePort.delete(VerificationSpec.resetPasswordAttemptsKey(email))
        emailSendPort.sendVerificationCode(email, code) { verificationCachePort.delete(cacheKey) }
    }

    override fun verifyCode(email: String, code: String) {
        val cacheKey = VerificationSpec.resetPasswordKey(email)
        val attemptsKey = VerificationSpec.resetPasswordAttemptsKey(email)
        verificationCodeValidator.validateCode(cacheKey, attemptsKey, code)
        verificationCachePort.save(
            cacheKey,
            VerificationSpec.RESET_PASSWORD_COMPLETED,
            VerificationSpec.verifiedSessionTimeToLive(),
        )
    }

    @Transactional
    override fun reset(command: PasswordResetCommand) {
        passwordResetVerificationValidator.requireVerified(command.email)
        passwordValidator.validatePasswordPattern(command.password)
        passwordValidator.validatePasswordMatch(command.password, command.passwordConfirm)

        val encodedPassword = passwordEncodePort.encode(command.password)
        val user = userQueryPort.loadByEmail(command.email)
        userCommandPort.updatePassword(user.id, encodedPassword)

        verificationCachePort.delete(VerificationSpec.resetPasswordKey(command.email))
    }
}