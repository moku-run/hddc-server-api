package dev.hddc.domains.hotdeal.adapter.`in`.web.sse

import com.fasterxml.jackson.annotation.JsonIgnore
import dev.hddc.domains.hotdeal.domain.event.DealEventType

abstract class DealSseEventMixin {
    @get:JsonIgnore
    abstract val eventType: DealEventType
}
