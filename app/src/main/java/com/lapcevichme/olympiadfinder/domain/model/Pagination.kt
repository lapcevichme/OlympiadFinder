package com.lapcevichme.olympiadfinder.domain.model

/**
 * Data-класс, представляющий доменную модель для метаданных пагинации.
 * Используется в бизнес-логике приложения.
 */
data class PaginationMetadata(
    val totalItems: Int,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int
)

/**
 * Data-класс, представляющий доменную модель для ответа с пагинацией.
 * Содержит список элементов типа [T] и метаданные пагинации.
 *
 * @param T Тип элементов, содержащихся в ответе.
 */
data class PaginatedResponse<T>(
    val items: List<T>,
    val meta: PaginationMetadata
)
