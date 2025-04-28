package com.lapcevichme.olympiadfinder.data.repository.mock

import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginatedResponse
import com.lapcevichme.olympiadfinder.domain.model.PaginationMetadata
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
            subjects = listOf(Subject("Предмет $i"), Subject("Математика"), Subject("Физика")),
            minGrade = i % 5 + 1,
            maxGrade = 11,
            stages = listOf(
                Stage(
                    "Этап 1",
                    LocalDate.now().plusDays(i.toLong() + 10),
                    LocalDate.now().plusDays(i.toLong() + 20)
                )
            ),
            link = "https://example.com/olympiad/$i",
            description = "Описание олимпиады $i. Это очень интересная олимпиада по предмету ${if (i % 2 == 0) "Математика" else "Физика"}.",
            keywords = "олимпиада $i, ключевое слово ${i % 3}"
        )
    }

    override fun getAllOlympiads(): Flow<Result<List<Olympiad>>> = flow {
        emit(Result.success(allOlympiads))
    }

    // Функция для получения олимпиад с пагинацией
    override fun getOlympiads(
        page: Int,
        pageSize: Int,
        query: String?,
        selectedGrades: List<Int>
    ): Flow<Result<PaginatedResponse<Olympiad>>> = flow {

        // Начинаем с полного списка
        var currentFilteredList = allOlympiads

        // 1. Применяем фильтрацию по запросу, если он есть
        if (!query.isNullOrBlank()) {
            val lowerCaseQuery = query.lowercase()
            currentFilteredList = currentFilteredList.filter { olympiad ->
                olympiad.name.lowercase().contains(lowerCaseQuery) ||
                        olympiad.description.orEmpty().lowercase().contains(lowerCaseQuery) ||
                        olympiad.keywords.orEmpty().lowercase().contains(lowerCaseQuery) ||
                        (olympiad.subjects ?: emptyList()).any { subject ->
                            subject.name.lowercase().contains(lowerCaseQuery)
                        }
            }
        }

        // <-- НОВОЕ: 2. Применяем фильтрацию по выбранным классам, если они есть
        if (selectedGrades.isNotEmpty()) {
            currentFilteredList = currentFilteredList.filter { olympiad ->
                // Олимпиада подходит, если хотя бы один выбранный класс попадает в диапазон ее minGrade-maxGrade
                // Обрабатываем случай, когда minGrade или maxGrade у олимпиады null
                if (olympiad.minGrade == null || olympiad.maxGrade == null) {
                    // Если у олимпиады не указан диапазон классов, она не подходит под конкретный фильтр по классу
                    false
                } else {
                    // Проверяем, что диапазон олимпиады (minGrade - maxGrade) пересекается с любым из selectedGrades
                    selectedGrades.any { selectedGrade ->
                        selectedGrade >= olympiad.minGrade && selectedGrade <= olympiad.maxGrade
                    }
                }
            }
        }


        // 3. Применяем пагинацию к ТЕКУЩЕМУ ОТФИЛЬТРОВАННОМУ списку (после поиска и фильтров по классам)
        val totalItems = currentFilteredList.size // Общее количество элементов после всех фильтров
        val totalPages = (totalItems + pageSize - 1) / pageSize

        val startIndex = (page - 1) * pageSize
        val safeStartIndex = maxOf(0, startIndex)
        val endIndex = minOf(safeStartIndex + pageSize, totalItems)

        val currentPageItems = if (safeStartIndex < endIndex) {
            currentFilteredList.subList(safeStartIndex, endIndex)
        } else {
            emptyList()
        }


        // 4. Создаем PaginatedResponse с данными и метаданными ТЕКУЩЕГО ОТФИЛЬТРОВАННОГО списка
        val response = PaginatedResponse(
            items = currentPageItems,
            meta = PaginationMetadata(
                totalItems = totalItems,
                totalPages = totalPages,
                currentPage = page,
                pageSize = pageSize
            )
        )

        // kotlinx.coroutines.delay(200)

        emit(Result.success(response))
    }

}