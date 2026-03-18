package dev.hddc.framework.mail

import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class MailContentProvider {

    fun loadVerificationCodeContent(code: String): String {
        val template = loadTemplate("template/mail/verification-code.html")
        return template.replace("{{CODE}}", code)
    }

    private fun loadTemplate(path: String): String {
        val resource = ClassPathResource(path)
        return resource.inputStream.bufferedReader(StandardCharsets.UTF_8).readText()
    }
}
