package dev.hddc.domains.user.application.service.command

import dev.hddc.domains.user.application.ports.input.command.EmailVerificationUsecase
import dev.hddc.domains.user.application.ports.output.command.EmailSendPort
import org.slf4j.LoggerFactory
import dev.hddc.domains.user.application.ports.output.command.VerificationCachePort
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import dev.hddc.domains.user.domain.policy.VerificationCodeGenerator
import dev.hddc.domains.user.domain.spec.VerificationSpec
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service

@Service
class EmailVerificationService(
    private val userQueryPort: UserQueryPort,
    private val verificationCachePort: VerificationCachePort,
    private val emailSendPort: EmailSendPort,
) : EmailVerificationUsecase {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun send(email: String) {
        require(!userQueryPort.existsByEmail(email)) {
            ApiResponseCode.USER_DUPLICATE_EMAIL.code
        }

        val code = VerificationCodeGenerator.generate()
        val cacheKey = VerificationSpec.signUpKey(email)

        verificationCachePort.save(cacheKey, code, VerificationSpec.codeTimeToLive())

        try {
            emailSendPort.sendVerificationCode(email, code)
        } catch (e: Exception) {
            log.error("이메일 발송 실패 - toEmail: {}", email, e)
            verificationCachePort.delete(cacheKey)
            throw IllegalStateException(ApiResponseCode.VERIFICATION_MAIL_SEND_FAILED.code)
        }
    }

    override fun verify(email: String, code: String) {
        val cacheKey = VerificationSpec.signUpKey(email)
        val attemptsKey = VerificationSpec.signUpAttemptsKey(email)

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
            VerificationSpec.COMPLETED,
            VerificationSpec.verifiedSessionTimeToLive(),
        )
    }

    private fun checkAttemptsNotExceeded(attemptsKey: String) {
        val attempts = verificationCachePort.getValue(attemptsKey)?.toLongOrNull() ?: 0L
        require(attempts < VerificationSpec.MAX_ATTEMPTS) {
            ApiResponseCode.VERIFICATION_ATTEMPTS_EXCEEDED.code
        }
    }
}
