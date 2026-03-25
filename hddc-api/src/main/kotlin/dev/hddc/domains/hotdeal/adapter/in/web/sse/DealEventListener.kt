package dev.hddc.domains.hotdeal.adapter.`in`.web.sse

import dev.hddc.domains.hotdeal.domain.event.DealSseEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class DealEventListener(
    private val sseEmitterManager: SseEmitterManager,
) {
    @Async
    @EventListener
    fun handle(event: DealSseEvent) {
        sseEmitterManager.broadcast(event)
    }
}
