package dev.hddc.domains.hotdeal.application.ports.output.checker

interface HotDealDuplicateChecker {
    fun existsByUrl(url: String): Boolean
}
