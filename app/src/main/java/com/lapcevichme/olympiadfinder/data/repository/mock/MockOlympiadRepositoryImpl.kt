package com.lapcevichme.olympiadfinder.data.repository.mock

import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

class MockOlympiadRepositoryImpl @Inject constructor() : OlympiadRepository {

    private val allOlympiads = (1..100).map { i ->
        Olympiad(
            id = i.toLong(),
            name = "Олимпиада $i",
            subjects = listOf(Subject("Предмет $i")),
            minGrade = i % 5 + 1,
            maxGrade = 11,
            stages = listOf(
                Stage(
                    "Этап 1",
                    LocalDate.now().plusDays(i.toLong()),
                    LocalDate.now().plusDays(i.toLong() + 10)
                )
            ),
            link = "https://example.com/olympiad/$i",
            description = "Описание олимпиады $i",
            keywords = "олимпиада, предмет $i"
        )
    }

    override fun getAllOlympiads(): Flow<List<Olympiad>> = flow {
        emit(allOlympiads)
    }

    // Функция для получения олимпиад с пагинацией
    override fun getOlympiads(page: Int, pageSize: Int): Flow<PaginatedResponse<Olympiad>> = flow {
        val startIndex = (page - 1) * pageSize
        val endIndex = minOf(startIndex + pageSize, allOlympiads.size)
        val currentPageItems = allOlympiads.subList(startIndex, endIndex)

        val totalPages = (allOlympiads.size + pageSize - 1) / pageSize // Округление вверх
        val response = PaginatedResponse(
            items = currentPageItems,
            meta = PaginationMetadata(
                totalItems = allOlympiads.size,
                totalPages = totalPages,
                currentPage = page,
                pageSize = pageSize
            )
        )
        emit(response)
    }
}

data class PaginatedResponse<T>(
    val items: List<T>,
    val meta: PaginationMetadata
)

data class PaginationMetadata(
    val totalItems: Int,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int
)