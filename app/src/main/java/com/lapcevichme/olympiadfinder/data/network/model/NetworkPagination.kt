package com.lapcevichme.olympiadfinder.data.network.model

import com.google.gson.annotations.SerializedName
import com.lapcevichme.olympiadfinder.domain.model.PaginationMetadata


data class NetworkPaginationMetadata(
    // @SerializedName, если имена полей в JSON отличаются (например, snake_case)
    @SerializedName("total_items") val totalItems: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("page_size") val pageSize: Int
)


data class NetworkPaginatedResponse<T>(
    @SerializedName("items") val items: List<T>,
    @SerializedName("meta") val meta: NetworkPaginationMetadata
)

fun NetworkPaginationMetadata.toDomain(): PaginationMetadata {
    return PaginationMetadata(
        totalItems = this.totalItems,
        totalPages = this.totalPages,
        currentPage = this.currentPage,
        pageSize = this.pageSize
    )
}
