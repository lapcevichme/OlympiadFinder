package com.lapcevichme.olympiadfinder.domain.repository

import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginatedResponse
import com.lapcevichme.olympiadfinder.domain.model.Resource
import com.lapcevichme.olympiadfinder.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface OlympiadRepository {
    fun getAllOlympiads(): Flow<Result<List<Olympiad>>>
    suspend fun getOlympiadById(id: Long): Resource<Olympiad>
    fun getPaginatedOlympiads(
        page: Int,
        pageSize: Int,
        query: String?,
        selectedGrades: List<Int>,
        selectedSubjects: List<Long>
    ): Flow<Resource<PaginatedResponse<Olympiad>>>
    suspend fun getAvailableSubjects(): Resource<List<Subject>>
}