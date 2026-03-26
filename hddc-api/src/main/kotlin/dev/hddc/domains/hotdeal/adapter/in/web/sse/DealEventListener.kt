package dev.hddc.domains.hotdeal.adapter.`in`.web.sse

import dev.hddc.domains.hotdeal.domain.event.DealSseEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class DealEventListener(
    private val sseEmitterManager: SseEmitterManager,
) {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: DealSseEvent) {
        sseEmitterManager.broadcast(event)
    }
}
