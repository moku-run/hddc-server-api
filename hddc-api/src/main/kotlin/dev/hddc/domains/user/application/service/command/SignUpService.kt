package dev.hddc.domains.user.application.service.command

import dev.hddc.domains.user.application.ports.input.command.SignUpCommand
import dev.hddc.domains.user.application.ports.input.command.SignUpResult
import dev.hddc.domains.user.application.ports.input.command.SignUpUsecase
import dev.hddc.domains.user.application.ports.output.command.UserCommandPort
import dev.hddc.domains.user.application.ports.output.command.VerificationCachePort
import dev.hddc.domains.user.application.ports.output.security.PasswordEncodePort
import dev.hddc.domains.user.application.ports.output.validation.EmailVerificationValidationPort
import dev.hddc.domains.user.application.ports.output.validation.UserValidationPort
import dev.hddc.domains.user.domain.model.UserModel
import dev.hddc.domains.user.domain.model.UserRole
import dev.hddc.domains.user.domain.spec.VerificationSpec
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignUpService(
    private val emailVerificationValidationPort: EmailVerificationValidationPort,
    private val userValidationPort: UserValidationPort,
    private val userCommandPort: UserCommandPort,
    private val verificationCachePort: VerificationCachePort,
    private val passwordEncodePort: PasswordEncodePort,
) : SignUpUsecase {

    override fun execute(command: SignUpCommand): SignUpResult {
        emailVerificationValidationPort.requireEmailVerified(command.email)
        val encodedPassword = passwordEncodePort.encode(command.password)
        return saveUser(command, encodedPassword)
    }

    @Transactional
    fun saveUser(command: SignUpCommand, encodedPassword: String): SignUpResult {
        userValidationPort.requireEmailNotExists(command.email)
        userValidationPort.requireNicknameNotExists(command.nickname)

        val model = UserModel(
            email = command.email,
            password = encodedPassword,
            nickname = command.nickname,
            role = UserRole.USER.name,
        )

        val userId = userCommandPort.create(model).id!!
        verificationCachePort.delete(VerificationSpec.signUpKey(command.email))
        return SignUpResult(userId = userId)
    }
}
