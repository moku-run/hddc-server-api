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
        // 1. validate
        userValidationPort.requireUserExistsByEmail(email)

        // 2. 코드 생성 + 캐시 저장
        val code = VerificationCodeGenerator.generate()
        val cacheKey = VerificationSpec.resetPasswordKey(email)
        verificationCachePort.save(cacheKey, code, VerificationSpec.codeTimeToLive())
        verificationCachePort.delete(VerificationSpec.resetPasswordAttemptsKey(email))

        // 3. 이메일 발송 (실패 시 캐시 정리)
        try {
            emailSendPort.sendVerificationCode(email, code)
        } catch (e: Exception) {
            verificationCachePort.delete(cacheKey)
            throw e
        }
    }

    override fun verifyCode(email: String, code: String) {
        val cacheKey = VerificationSpec.resetPasswordKey(email)
        val attemptsKey = VerificationSpec.resetPasswordAttemptsKey(email)

        // 1. validate (코드 검증 + 시도 횟수 체크)
        verificationCodeValidator.validateCode(cacheKey, attemptsKey, code)

        // 2. 인증 완료 상태 저장
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
        val user = userQueryPort.loadByEmail(email)
        userCommandPort.updatePassword(user.id!!, encodedPassword)
    }
}
