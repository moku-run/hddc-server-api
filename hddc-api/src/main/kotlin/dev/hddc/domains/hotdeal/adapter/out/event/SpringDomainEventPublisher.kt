package dev.hddc.domains.hotdeal.adapter.out.event

import dev.hddc.domains.hotdeal.application.ports.output.event.DomainEventPublisher
import dev.hddc.domains.hotdeal.domain.event.DealSseEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SpringDomainEventPublisher(
    private val publisher: ApplicationEventPublisher,
) : DomainEventPublisher {
    override fun publish(event: DealSseEvent) = publisher.publishEvent(event)
}
