package com.lapcevichme.olympiadfinder.domain.repository

import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginatedResponse
import kotlinx.coroutines.flow.Flow

interface OlympiadRepository {
    fun getAllOlympiads(): Flow<List<Olympiad>>
    fun getOlympiads(page: Int, pageSize: Int, query: String?): Flow<PaginatedResponse<Olympiad>>
}