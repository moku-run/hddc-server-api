package dev.hddc.domains.user.application.service.command

import dev.hddc.domains.user.application.ports.input.command.SignUpCommand
import dev.hddc.domains.user.application.ports.input.command.SignUpResult
import dev.hddc.domains.user.application.ports.input.command.SignUpUsecase
import dev.hddc.domains.user.application.ports.output.command.UserCommandPort
import dev.hddc.domains.user.application.ports.output.command.VerificationCachePort
import dev.hddc.domains.user.application.ports.output.security.PasswordEncodePort
import dev.hddc.domains.user.application.ports.output.validation.EmailVerificationValidationPort
import dev.hddc.domains.user.application.ports.output.validation.PasswordValidator
import dev.hddc.domains.user.application.ports.output.validation.UserValidationPort
import dev.hddc.domains.user.domain.model.UserModel
import dev.hddc.domains.user.domain.model.UserRole
import dev.hddc.domains.user.domain.spec.VerificationSpec
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignUpService(
    private val emailVerificationValidationPort: EmailVerificationValidationPort,
    private val passwordValidator: PasswordValidator,
    private val userValidationPort: UserValidationPort,
    private val userCommandPort: UserCommandPort,
    private val verificationCachePort: VerificationCachePort,
    private val passwordEncodePort: PasswordEncodePort,
) : SignUpUsecase {

    @Transactional
    override fun execute(command: SignUpCommand): SignUpResult {
        // 1. validate (트랜잭션 밖이어도 되지만, 프록시 entry point에 @Transactional 필요)
        emailVerificationValidationPort.requireEmailVerified(command.email)
        passwordValidator.validatePasswordPattern(command.password)
        userValidationPort.requireEmailNotExists(command.email)
        userValidationPort.requireNicknameNotExists(command.nickname)

        // 2. save
        val encodedPassword = passwordEncodePort.encode(command.password)
        val model = UserModel(
            email = command.email,
            password = encodedPassword,
            nickname = command.nickname,
            role = UserRole.USER.name,
        )
        val userId = userCommandPort.create(model).id!!

        // 3. cleanup
        verificationCachePort.delete(VerificationSpec.signUpKey(command.email))

        return SignUpResult(userId = userId)
    }
}
