package com.lapcevichme.olympiadfinder.domain.repository

import com.lapcevichme.olympiadfinder.data.repository.mock.PaginatedResponse
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import kotlinx.coroutines.flow.Flow

interface OlympiadRepository {
    fun getAllOlympiads(): Flow<List<Olympiad>>
    fun getOlympiads(page: Int, pageSize: Int): Flow<PaginatedResponse<Olympiad>>
}