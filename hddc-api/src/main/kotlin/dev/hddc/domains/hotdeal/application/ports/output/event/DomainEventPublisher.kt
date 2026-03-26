package dev.hddc.domains.hotdeal.application.ports.output.event

import dev.hddc.domains.hotdeal.domain.event.DealSseEvent

interface DomainEventPublisher {
    fun publish(event: DealSseEvent)
}
