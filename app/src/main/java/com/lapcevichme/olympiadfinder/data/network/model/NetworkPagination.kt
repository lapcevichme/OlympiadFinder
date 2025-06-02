package com.lapcevichme.olympiadfinder.data.network.model

import com.google.gson.annotations.SerializedName
import com.lapcevichme.olympiadfinder.domain.model.PaginationMetadata

/**
 * Data class representing the network response for pagination metadata.
 */
data class NetworkPaginationMetadata(
    @SerializedName("total_items") val totalItems: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("page_size") val pageSize: Int
)

/**
 * Data class representing a paginated network response containing a list of items and metadata.
 */
data class NetworkPaginatedResponse<T>(
    @SerializedName("items") val items: List<T>,
    @SerializedName("meta") val meta: NetworkPaginationMetadata
)

/**
 * Extension function to map a [NetworkPaginationMetadata] to the domain model [PaginationMetadata].
 */
fun NetworkPaginationMetadata.toDomain(): PaginationMetadata {
    return PaginationMetadata(
        totalItems = this.totalItems,
        totalPages = this.totalPages,
        currentPage = this.currentPage,
        pageSize = this.pageSize
    )
}