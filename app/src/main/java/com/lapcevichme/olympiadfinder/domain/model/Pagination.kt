package com.lapcevichme.olympiadfinder.domain.model

data class PaginationMetadata(
    val totalItems: Int,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int
)

data class PaginatedResponse<T>(
    val items: List<T>,
    val meta: PaginationMetadata
)
