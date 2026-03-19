package dev.hddc.framework.upload

import org.springframework.boot.context.properties.ConfigurationProperties
import software.amazon.awssdk.regions.Region
import java.time.Duration

@ConfigurationProperties(prefix = "r2")
data class R2Properties(
    val endpoint: String,
    val bucketName: String,
    val accessKey: String,
    val secretKey: String,
    val presigned: Presigned,
) {
    val getExpiredTTL: Duration get() = Duration.ofSeconds(presigned.getExpired.toLong())
    val putExpiredTTL: Duration get() = Duration.ofSeconds(presigned.putExpired.toLong())
    val region: Region get() = Region.of(presigned.regionName)

    data class Presigned(
        val getExpired: String,
        val putExpired: String,
        val regionName: String,
    )
}
