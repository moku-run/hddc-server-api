package dev.hddc.domains.user.application.service.command

import dev.hddc.domains.profile.application.ports.output.command.CreateDefaultProfilePort
import dev.hddc.domains.user.application.ports.input.command.SignUpCommand
import dev.hddc.domains.user.application.ports.input.command.SignUpUsecase
import dev.hddc.domains.user.application.ports.output.command.UserCommandPort
import dev.hddc.domains.user.application.ports.output.command.VerificationCachePort
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import dev.hddc.domains.user.domain.model.UserModel
import dev.hddc.domains.user.domain.model.UserRole
import dev.hddc.domains.user.domain.spec.VerificationSpec
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignUpService(
    private val userQueryPort: UserQueryPort,
    private val userCommandPort: UserCommandPort,
    private val verificationCachePort: VerificationCachePort,
    private val passwordEncoder: PasswordEncoder,
    private val createDefaultProfilePort: CreateDefaultProfilePort,
) : SignUpUsecase {

    @Transactional
    override fun execute(command: SignUpCommand): Long {
        requireEmailVerified(command.email)

        require(!userQueryPort.existsByEmail(command.email)) {
            ApiResponseCode.USER_DUPLICATE_EMAIL.code
        }

        val model = UserModel(
            email = command.email,
            password = passwordEncoder.encode(command.password),
            nickname = command.nickname,
            role = UserRole.USER.name,
        )

        val userId = userCommandPort.save(model).id!!
        verificationCachePort.delete(VerificationSpec.signUpKey(command.email))

        createDefaultProfilePort.createDefaultProfile(userId, command.nickname)

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
