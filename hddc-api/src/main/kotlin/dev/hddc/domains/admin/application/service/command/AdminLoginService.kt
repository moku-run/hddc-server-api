package dev.hddc.domains.admin.application.service.command

import dev.hddc.domains.admin.application.ports.input.command.AdminLoginCommand
import dev.hddc.domains.admin.application.ports.input.command.AdminLoginResult
import dev.hddc.domains.admin.application.ports.input.command.AdminLoginUsecase
import dev.hddc.domains.admin.application.ports.output.query.AdminQueryPort
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.security.jwt.JwtCreator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AdminLoginService(
    private val adminQueryPort: AdminQueryPort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtCreator: JwtCreator,
) : AdminLoginUsecase {

    override fun execute(command: AdminLoginCommand): AdminLoginResult {
        val admin = adminQueryPort.findByEmail(command.email)
            ?: throw IllegalArgumentException(ApiResponseCode.INVALID_CREDENTIALS.code)

        require(passwordEncoder.matches(command.password, admin.password)) {
            ApiResponseCode.INVALID_CREDENTIALS.code
        }

        val token = jwtCreator.create(admin.email, admin.role)

        return AdminLoginResult(
            token = token,
            name = admin.name,
            role = admin.role,
        )
    }
}
