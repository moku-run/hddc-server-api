package dev.hddc.domains.hotdeal.adapter.`in`.web.sse

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import dev.hddc.domains.hotdeal.domain.event.DealSseEvent
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.CopyOnWriteArrayList

@Component
class SseEmitterManager(
    objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val emitters = CopyOnWriteArrayList<SseEmitter>()

    // SSE 전용 ObjectMapper: null 제외 + eventType 제외
    private val sseMapper = objectMapper.copy().apply {
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        addMixIn(DealSseEvent::class.java, DealSseEventMixin::class.java)
    }

    fun add(emitter: SseEmitter): SseEmitter {
        emitters.add(emitter)
        emitter.onCompletion { emitters.remove(emitter) }
        emitter.onTimeout { emitters.remove(emitter) }
        emitter.onError { emitters.remove(emitter) }
        log.debug("[SSE] 클라이언트 연결 (현재 {}명)", emitters.size)
        return emitter
    }

    fun broadcast(event: DealSseEvent) {
        val data = sseMapper.writeValueAsString(event)
        val dead = mutableListOf<SseEmitter>()

        emitters.forEach { emitter ->
            try {
                emitter.send(
                    SseEmitter.event()
                        .name(event.eventType)
                        .data(data)
                )
            } catch (_: Exception) {
                dead.add(emitter)
            }
        }

        if (dead.isNotEmpty()) {
            emitters.removeAll(dead.toSet())
            log.debug("[SSE] 끊긴 연결 {}개 제거 (현재 {}명)", dead.size, emitters.size)
        }
    }

    @Scheduled(fixedRate = 30_000)
    fun heartbeat() {
        if (emitters.isEmpty()) return

        val dead = mutableListOf<SseEmitter>()
        emitters.forEach { emitter ->
            try {
                emitter.send(SseEmitter.event().comment("heartbeat"))
            } catch (_: Exception) {
                dead.add(emitter)
            }
        }

        if (dead.isNotEmpty()) {
            emitters.removeAll(dead.toSet())
        }
    }

    fun connectionCount(): Int = emitters.size
}
