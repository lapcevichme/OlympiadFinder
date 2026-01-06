package com.lapcevichme.olympiadfinder.data.network.model

import com.google.gson.annotations.SerializedName
import com.lapcevichme.olympiadfinder.domain.model.PaginationMetadata

/**
 * Data-класс, представляющий сетевой ответ для метаданных пагинации.
 * Используется для десериализации информации о пагинации из сетевых запросов.
 */
data class NetworkPaginationMetadata(
    @SerializedName("total_items") val totalItems: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("page_size") val pageSize: Int
)

/**
 * Data-класс, представляющий общий сетевой ответ с пагинацией.
 * Содержит список элементов типа [T] и метаданные пагинации.
 *
 * @param T Тип элементов, содержащихся в ответе.
 */
data class NetworkPaginatedResponse<T>(
    @SerializedName("items") val items: List<T>,
    @SerializedName("meta") val meta: NetworkPaginationMetadata
)

/**
 * Расширяющая функция для преобразования [NetworkPaginationMetadata] в доменную модель [PaginationMetadata].
 *
 * @return Доменная модель [PaginationMetadata], представляющая данные пагинации.
 */
fun NetworkPaginationMetadata.toDomain(): PaginationMetadata {
    return PaginationMetadata(
        totalItems = this.totalItems,
        totalPages = this.totalPages,
        currentPage = this.currentPage,
        pageSize = this.pageSize
    )
}