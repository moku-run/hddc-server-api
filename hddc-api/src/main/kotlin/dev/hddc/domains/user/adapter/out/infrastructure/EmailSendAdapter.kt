package dev.hddc.domains.user.adapter.out.infrastructure

import dev.hddc.domains.user.application.ports.output.command.EmailSendPort
import dev.hddc.framework.mail.MailContentProvider
import dev.hddc.framework.mail.MailService
import org.springframework.stereotype.Component

@Component
class EmailSendAdapter(
    private val mailService: MailService,
    private val mailContentProvider: MailContentProvider,
) : EmailSendPort {

    override fun sendVerificationCode(toEmail: String, code: String) {
        mailService.send(
            toEmail = toEmail,
            subject = "[HDDC] 이메일 인증 코드",
            content = mailContentProvider.loadVerificationCodeContent(code),
        )
    }

    override fun sendVerificationCode(toEmail: String, code: String, onFailure: () -> Unit) {
        try {
            sendVerificationCode(toEmail, code)
        } catch (e: Exception) {
            onFailure()
            throw e
        }
    }
}
