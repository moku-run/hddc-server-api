package dev.hddc.framework.mail

import jakarta.mail.MessagingException
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class MailService(
    private val javaMailSender: JavaMailSender,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun send(toEmail: String, subject: String, content: String) {
        try {
            val message = javaMailSender.createMimeMessage()
            MimeMessageHelper(message, false, StandardCharsets.UTF_8.name()).apply {
                setTo(toEmail)
                setSubject(subject)
                setText(content, true)
            }
            javaMailSender.send(message)
        } catch (e: MessagingException) {
            log.error("메일 발송 실패 - toEmail: {}", toEmail, e)
            throw e
        }
    }
}
