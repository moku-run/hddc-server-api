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
        // 1. validate
        userValidationPort.requireEmailNotExists(email)

        // 2. 코드 생성 + 캐시 저장
        val code = VerificationCodeGenerator.generate()
        val cacheKey = VerificationSpec.signUpKey(email)
        verificationCachePort.save(cacheKey, code, VerificationSpec.codeTimeToLive())

        // 3. 이메일 발송 (실패 시 캐시 정리)
        try {
            emailSendPort.sendVerificationCode(email, code)
        } catch (e: Exception) {
            verificationCachePort.delete(cacheKey)
            throw IllegalStateException("VERIFICATION_MAIL_SEND_FAILED")
        }
    }

    override fun verify(email: String, code: String) {
        val cacheKey = VerificationSpec.signUpKey(email)
        val attemptsKey = VerificationSpec.signUpAttemptsKey(email)

        // 1. validate (코드 검증 + 시도 횟수 체크)
        verificationCodeValidator.validateCode(cacheKey, attemptsKey, code)

        // 2. 인증 완료 상태 저장
        verificationCachePort.save(
            cacheKey,
            VerificationSpec.COMPLETED,
            VerificationSpec.verifiedSessionTimeToLive(),
        )
    }
}
