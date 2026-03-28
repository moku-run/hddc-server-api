package dev.hddc.domains.hotdeal.application.ports.output.event

import dev.hddc.domains.hotdeal.domain.event.DealEvent

interface DomainEventPublisher {
    fun publish(event: DealEvent)
}
