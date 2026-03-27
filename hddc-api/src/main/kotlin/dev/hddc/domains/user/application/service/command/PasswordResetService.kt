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
) : PasswordResetUsecase {

    override fun sendCode(email: String) {
        userValidationPort.requireUserExistsByEmail(email)

        val code = VerificationCodeGenerator.generate()
        val cacheKey = VerificationSpec.resetPasswordKey(email)
        val attemptsKey = VerificationSpec.resetPasswordAttemptsKey(email)

        verificationCachePort.save(cacheKey, code, VerificationSpec.codeTimeToLive())
        verificationCachePort.delete(attemptsKey)

        try {
            emailSendPort.sendVerificationCode(email, code)
        } catch (e: Exception) {
            verificationCachePort.delete(cacheKey)
            throw IllegalStateException("VERIFICATION_MAIL_SEND_FAILED")
        }
    }

    override fun verifyCode(email: String, code: String) {
        val cacheKey = VerificationSpec.resetPasswordKey(email)
        val attemptsKey = VerificationSpec.resetPasswordAttemptsKey(email)

        checkAttemptsNotExceeded(attemptsKey)

        val storedCode = verificationCachePort.getValue(cacheKey)
            ?: throw IllegalArgumentException("VERIFICATION_EXPIRED")

        if (storedCode != code) {
            val attempts = verificationCachePort.increment(attemptsKey, VerificationSpec.codeTimeToLive())
            if (attempts >= VerificationSpec.MAX_ATTEMPTS) {
                throw IllegalArgumentException("VERIFICATION_ATTEMPTS_EXCEEDED")
            }
            throw IllegalArgumentException("VERIFICATION_INVALID_CODE")
        }

        verificationCachePort.delete(attemptsKey)
        verificationCachePort.save(
            cacheKey,
            VerificationSpec.RESET_PASSWORD_COMPLETED,
            VerificationSpec.verifiedSessionTimeToLive(),
        )
    }

    override fun reset(command: PasswordResetCommand) {
        // 1. validate
        passwordResetVerificationValidator.requireVerified(command.email)
        passwordValidator.validatePasswordPattern(command.password)
        passwordValidator.validatePasswordMatch(command.password, command.passwordConfirm)

        // 2. save
        val encodedPassword = passwordEncodePort.encode(command.password)
        savePassword(command.email, encodedPassword)

        // 3. cleanup
        verificationCachePort.delete(VerificationSpec.resetPasswordKey(command.email))
    }

    @Transactional
    fun savePassword(email: String, encodedPassword: String) {
        val user = userQueryPort.findByEmail(email)
            ?: throw IllegalArgumentException("USER_NOT_FOUND")
        userCommandPort.updatePassword(user.id!!, encodedPassword)
    }

    private fun checkAttemptsNotExceeded(attemptsKey: String) {
        val attempts = verificationCachePort.getValue(attemptsKey)?.toLongOrNull() ?: 0L
        require(attempts < VerificationSpec.MAX_ATTEMPTS) {
            "VERIFICATION_ATTEMPTS_EXCEEDED"
        }
    }
}
