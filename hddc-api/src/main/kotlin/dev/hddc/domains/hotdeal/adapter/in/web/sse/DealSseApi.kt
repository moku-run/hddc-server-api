package dev.hddc.domains.hotdeal.adapter.`in`.web.sse

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Tag(name = "SSE", description = "실시간 이벤트 스트림")
@RestController
class DealSseApi(
    private val sseEmitterManager: SseEmitterManager,
) {
    @Operation(summary = "SSE 이벤트 스트림 연결")
    @GetMapping("/api/events/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun stream(): SseEmitter {
        val emitter = SseEmitter(0L) // 타임아웃 없음 (heartbeat로 유지)
        return sseEmitterManager.add(emitter)
    }
}
