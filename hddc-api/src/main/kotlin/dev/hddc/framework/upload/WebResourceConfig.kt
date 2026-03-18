package dev.hddc.framework.upload

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebResourceConfig(
    @Value("\${upload.path:./uploads}")
    private val uploadPath: String,
) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/public/uploads/**")
            .addResourceLocations("file:$uploadPath/")
    }
}
