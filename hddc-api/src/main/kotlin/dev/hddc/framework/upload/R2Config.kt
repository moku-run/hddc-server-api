package dev.hddc.framework.upload

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
@EnableConfigurationProperties(R2Properties::class)
class R2Config(
    private val r2Properties: R2Properties,
) {
    private fun credentials() = StaticCredentialsProvider.create(
        AwsBasicCredentials.create(r2Properties.accessKey, r2Properties.secretKey)
    )

    @Bean
    fun s3Client(): S3Client = S3Client.builder()
        .endpointOverride(URI.create(r2Properties.endpoint))
        .credentialsProvider(credentials())
        .region(r2Properties.region)
        .serviceConfiguration(
            S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build()
        )
        .build()

    @Bean
    fun s3Presigner(): S3Presigner = S3Presigner.builder()
        .endpointOverride(URI.create(r2Properties.endpoint))
        .credentialsProvider(credentials())
        .region(r2Properties.region)
        .serviceConfiguration(
            S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build()
        )
        .build()
}
