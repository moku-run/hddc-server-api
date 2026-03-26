package dev.hddc.domains.user.application.service.command

import dev.hddc.domains.user.application.ports.input.command.SignUpCommand
import dev.hddc.domains.user.application.ports.input.command.SignUpUsecase
import dev.hddc.domains.user.application.ports.output.command.UserCommandPort
import dev.hddc.domains.user.application.ports.output.command.VerificationCachePort
import dev.hddc.domains.user.application.ports.output.validation.UserValidationPort
import dev.hddc.domains.user.domain.model.UserModel
import dev.hddc.domains.user.domain.model.UserRole
import dev.hddc.domains.user.domain.spec.VerificationSpec
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignUpService(
    private val userValidationPort: UserValidationPort,
    private val userCommandPort: UserCommandPort,
    private val verificationCachePort: VerificationCachePort,
    private val passwordEncoder: PasswordEncoder,
) : SignUpUsecase {

    override fun execute(command: SignUpCommand): Long {
        requireEmailVerified(command.email)
        val encodedPassword = passwordEncoder.encode(command.password)
        return saveUser(command, encodedPassword)
    }

    @Transactional
    fun saveUser(command: SignUpCommand, encodedPassword: String): Long {
        userValidationPort.requireEmailNotExists(command.email)
        userValidationPort.requireNicknameNotExists(command.nickname)

        val model = UserModel(
            email = command.email,
            password = encodedPassword,
            nickname = command.nickname,
            role = UserRole.USER.name,
        )

        val userId = userCommandPort.save(model).id!!
        verificationCachePort.delete(VerificationSpec.signUpKey(command.email))
        return userId
    }

    private fun requireEmailVerified(email: String) {
        val cacheKey = VerificationSpec.signUpKey(email)
        val value = verificationCachePort.getValue(cacheKey)
        require(value == VerificationSpec.COMPLETED) {
            ApiResponseCode.VERIFICATION_REQUIRED.code
        }
    }
}
