package dev.hddc.domains.user.application.service.command

import dev.hddc.domains.user.application.ports.input.command.LoginCommand
import dev.hddc.domains.user.application.ports.input.command.LoginResult
import dev.hddc.domains.user.application.ports.input.command.LoginUsecase
import dev.hddc.domains.user.application.ports.output.command.UserCommandPort
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import dev.hddc.domains.user.application.ports.output.security.TokenPort
import dev.hddc.domains.user.application.ports.output.validation.LoginValidator
import dev.hddc.domains.user.domain.spec.LoginSpec
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LoginService(
    private val userQueryPort: UserQueryPort,
    private val userCommandPort: UserCommandPort,
    private val loginValidator: LoginValidator,
    private val tokenPort: TokenPort,
) : LoginUsecase {

    @Transactional
    override fun execute(command: LoginCommand): LoginResult {
        val user = userQueryPort.loadByEmail(command.email)

        // 1. validate
        loginValidator.requireActiveUser(user)

        // 2. password check + login attempt 처리
        try {
            loginValidator.requirePasswordMatch(command.password, user.password)
        } catch (e: Exception) {
            val newCount = user.loginAttemptCount + 1
            userCommandPort.updateLoginFailed(user.id!!, newCount, LoginSpec.isLockRequired(newCount))
            throw e
        }

        // 3. save (로그인 성공)
        userCommandPort.updateLoginSuccess(user.id!!)

        // 4. token 발급
        val tokenPair = tokenPort.createTokenPair(user.email, user.role)

        return LoginResult(
            userId = user.id,
            email = user.email,
            nickname = user.nickname,
            role = user.role,
            accessToken = tokenPair.accessToken,
            refreshToken = tokenPair.refreshToken,
        )
    }
}
