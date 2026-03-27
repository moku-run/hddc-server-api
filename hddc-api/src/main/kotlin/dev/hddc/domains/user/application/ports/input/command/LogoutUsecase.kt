package dev.hddc.domains.user.application.ports.input.command

interface LogoutUsecase {
    fun execute(accessToken: String?)
}
