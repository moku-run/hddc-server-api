package dev.hddc.domains.user.application.service.command

import dev.hddc.domains.user.application.ports.input.command.LoginCommand
import dev.hddc.domains.user.application.ports.input.command.LoginResult
import dev.hddc.domains.user.application.ports.input.command.LoginUsecase
import dev.hddc.domains.user.application.ports.output.command.UserCommandPort
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.security.jwt.JwtService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class LoginService(
    private val userQueryPort: UserQueryPort,
    private val userCommandPort: UserCommandPort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) : LoginUsecase {

    @Transactional
    override fun execute(command: LoginCommand): LoginResult {
        val user = userQueryPort.findByEmail(command.email)
            ?: throw IllegalArgumentException(ApiResponseCode.INVALID_CREDENTIALS.code)

        require(!user.isDeleted) { ApiResponseCode.USER_DELETED.code }
        require(!user.isLocked) { ApiResponseCode.ACCOUNT_LOCKED.code }

        if (!passwordEncoder.matches(command.password, user.password)) {
            val updated = user.copy(
                loginAttemptCount = user.loginAttemptCount + 1,
                isLocked = user.loginAttemptCount + 1 >= MAX_LOGIN_ATTEMPTS,
            )
            userCommandPort.save(updated)
            throw IllegalArgumentException(ApiResponseCode.INVALID_CREDENTIALS.code)
        }

        val updated = user.copy(
            loginAttemptCount = 0,
            lastLoginAt = Instant.now(),
        )
        userCommandPort.save(updated)

        val token = jwtService.create(user.email, user.role)

        return LoginResult(
            userId = user.id!!,
            email = user.email,
            nickname = user.nickname,
            role = user.role,
            token = token,
        )
    }

    companion object {
        private const val MAX_LOGIN_ATTEMPTS = 5
    }
}
