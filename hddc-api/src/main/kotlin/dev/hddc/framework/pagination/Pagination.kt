package dev.hddc.framework.pagination

import org.springframework.data.domain.Page

data class Pagination(
    val totalItems: Long,
    val totalPages: Int,
    val currentPage: Int,
    val perPage: Int,
) {
    companion object {
        fun of(page: Page<*>): Pagination =
            Pagination(
                totalItems = page.totalElements,
                totalPages = page.totalPages,
                currentPage = page.number + 1,
                perPage = page.size,
            )

        fun of(totalItems: Long, totalPages: Int, page: Int, size: Int): Pagination =
            Pagination(
                totalItems = totalItems,
                totalPages = totalPages,
                currentPage = page + 1,
                perPage = size,
            )
    }
}
