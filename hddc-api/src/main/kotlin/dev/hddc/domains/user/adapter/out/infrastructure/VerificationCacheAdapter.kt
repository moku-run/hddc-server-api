package dev.hddc.domains.user.adapter.out.infrastructure

import dev.hddc.domains.user.application.ports.output.command.VerificationCachePort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class VerificationCacheAdapter(
    private val redisTemplate: StringRedisTemplate,
) : VerificationCachePort {

    override fun save(key: String, value: String, ttl: Duration) {
        redisTemplate.opsForValue().set(key, value, ttl)
    }

    override fun getValue(key: String): String? =
        redisTemplate.opsForValue().get(key)

    override fun delete(key: String) {
        redisTemplate.delete(key)
    }

    override fun increment(key: String, ttl: Duration): Long {
        val count = redisTemplate.opsForValue().increment(key) ?: 1L
        if (count == 1L) {
            redisTemplate.expire(key, ttl)
        }
        return count
    }
}
