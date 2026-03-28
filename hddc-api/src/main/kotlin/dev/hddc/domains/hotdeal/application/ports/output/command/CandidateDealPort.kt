package dev.hddc.domains.hotdeal.application.ports.output.command

interface CandidateDealPort {
    fun updateStatus(id: Long, status: String, transferredAt: java.time.Instant? = null)
}
