package dev.hddc.domains.user.application.service.command

import dev.hddc.domains.user.application.ports.input.command.PasswordResetCommand
import dev.hddc.domains.user.application.ports.input.command.PasswordResetUsecase
import dev.hddc.domains.user.application.ports.output.command.EmailSendPort
import dev.hddc.domains.user.application.ports.output.command.UserCommandPort
import dev.hddc.domains.user.application.ports.output.command.VerificationCachePort
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import dev.hddc.domains.user.domain.spec.PasswordSpec
import dev.hddc.domains.user.domain.policy.VerificationCodeGenerator
import dev.hddc.domains.user.domain.spec.VerificationSpec
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PasswordResetService(
    private val userQueryPort: UserQueryPort,
    private val userCommandPort: UserCommandPort,
    private val verificationCachePort: VerificationCachePort,
    private val emailSendPort: EmailSendPort,
    private val passwordEncoder: PasswordEncoder,
) : PasswordResetUsecase {

    override fun sendCode(email: String) {
        requireNotNull(userQueryPort.findByEmail(email)) {
            ApiResponseCode.USER_NOT_FOUND.code
        }

        val code = VerificationCodeGenerator.generate()
        val cacheKey = VerificationSpec.resetPasswordKey(email)
        val attemptsKey = VerificationSpec.resetPasswordAttemptsKey(email)

        verificationCachePort.save(cacheKey, code, VerificationSpec.codeTimeToLive())
        verificationCachePort.delete(attemptsKey)

        try {
            emailSendPort.sendVerificationCode(email, code)
        } catch (e: Exception) {
            verificationCachePort.delete(cacheKey)
            throw IllegalStateException(ApiResponseCode.VERIFICATION_MAIL_SEND_FAILED.code)
        }
    }

    override fun verifyCode(email: String, code: String) {
        val cacheKey = VerificationSpec.resetPasswordKey(email)
        val attemptsKey = VerificationSpec.resetPasswordAttemptsKey(email)

        checkAttemptsNotExceeded(attemptsKey)

        val storedCode = verificationCachePort.getValue(cacheKey)
            ?: throw IllegalArgumentException(ApiResponseCode.VERIFICATION_EXPIRED.code)

        if (storedCode != code) {
            val attempts = verificationCachePort.increment(attemptsKey, VerificationSpec.codeTimeToLive())
            if (attempts >= VerificationSpec.MAX_ATTEMPTS) {
                throw IllegalArgumentException(ApiResponseCode.VERIFICATION_ATTEMPTS_EXCEEDED.code)
            }
            throw IllegalArgumentException(ApiResponseCode.VERIFICATION_INVALID_CODE.code)
        }

        verificationCachePort.delete(attemptsKey)
        verificationCachePort.save(
            cacheKey,
            VerificationSpec.RESET_PASSWORD_COMPLETED,
            VerificationSpec.verifiedSessionTimeToLive(),
        )
    }

    @Transactional
    override fun reset(command: PasswordResetCommand) {
        val cacheKey = VerificationSpec.resetPasswordKey(command.email)

        requireVerified(cacheKey)
        validatePassword(command.password, command.passwordConfirm)

        val user = userQueryPort.findByEmail(command.email)
            ?: throw IllegalArgumentException(ApiResponseCode.USER_NOT_FOUND.code)

        val updated = user.copy(
            password = passwordEncoder.encode(command.password),
            loginAttemptCount = 0,
            isLocked = false,
        )
        userCommandPort.save(updated)

        verificationCachePort.delete(cacheKey)
    }

    private fun requireVerified(cacheKey: String) {
        val value = verificationCachePort.getValue(cacheKey)
        require(value == VerificationSpec.RESET_PASSWORD_COMPLETED) {
            ApiResponseCode.VERIFICATION_REQUIRED.code
        }
    }

    private fun validatePassword(password: String, passwordConfirm: String) {
        require(PasswordSpec.validate(password)) {
            ApiResponseCode.USER_PASSWORD_MISMATCH.code
        }
        require(password == passwordConfirm) {
            ApiResponseCode.USER_PASSWORD_MISMATCH.code
        }
    }

    private fun checkAttemptsNotExceeded(attemptsKey: String) {
        val attempts = verificationCachePort.getValue(attemptsKey)?.toLongOrNull() ?: 0L
        require(attempts < VerificationSpec.MAX_ATTEMPTS) {
            ApiResponseCode.VERIFICATION_ATTEMPTS_EXCEEDED.code
        }
    }
}
