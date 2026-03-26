package dev.hddc.domains.hotdeal.adapter.`in`.web.sse

import com.fasterxml.jackson.annotation.JsonIgnore

abstract class DealSseEventMixin {
    @get:JsonIgnore
    abstract val eventType: String
}
