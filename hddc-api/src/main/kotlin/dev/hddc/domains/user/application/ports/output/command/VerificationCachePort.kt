package dev.hddc.domains.user.application.ports.output.command

import java.time.Duration

interface VerificationCachePort {
    fun save(key: String, value: String, ttl: Duration)
    fun getValue(key: String): String?
    fun delete(key: String)
    fun increment(key: String, ttl: Duration): Long
}
