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

    override fun getAllOlympiads(): Flow<List<Olympiad>> = flow {
        emit(allOlympiads)
    }

    // Функция для получения олимпиад с пагинацией
    override fun getOlympiads(
        page: Int,
        pageSize: Int,
        query: String?
    ): Flow<PaginatedResponse<Olympiad>> = flow {

        val filteredOlympiads = if (query.isNullOrBlank()) {
            // Если запрос пуст или null, используем полный список
            allOlympiads
        } else {
            val lowerCaseQuery = query.lowercase()

            allOlympiads.filter { olympiad ->
                olympiad.name.lowercase().contains(lowerCaseQuery) ||
                        olympiad.description.orEmpty().lowercase().contains(lowerCaseQuery) ||
                        olympiad.keywords.orEmpty().lowercase().contains(lowerCaseQuery) ||
                        (olympiad.subjects ?: emptyList()).any { subject ->
                            subject.name.lowercase().contains(lowerCaseQuery)
                        }
            }
        }

        // Применяем пагинацию к ОТФИЛЬТРОВАННОМУ списку
        val totalItems = filteredOlympiads.size // Общее количество элементов после фильтрации
        val totalPages = (totalItems + pageSize - 1) / pageSize // Общее количество страниц для отфильтрованного списка (округление вверх)

        // Рассчитываем индексы для текущей страницы в ОТФИЛЬТРОВАННОМ списке
        val startIndex = (page - 1) * pageSize
        val endIndex = minOf(startIndex + pageSize, totalItems)

        // Получаем элементы для текущей страницы из ОТФИЛЬТРОВАННОГО списка
        // Проверяем, что startIndex не выходит за пределы списка и что startIndex < endIndex
        val currentPageItems = if (startIndex < totalItems) {
            filteredOlympiads.subList(startIndex, endIndex)
        } else {
            emptyList() // Если запрошенная страница за пределами или список пуст
        }


        // Создаем PaginatedResponse с данными и метаданными ОТФИЛЬТРОВАННОГО списка
        val response = PaginatedResponse(
            items = currentPageItems, // Элементы для текущей страницы
            meta = PaginationMetadata(
                totalItems = totalItems,
                totalPages = totalPages,
                currentPage = page,
                pageSize = pageSize
            )
        )

        // Добавим небольшую задержку, чтобы имитировать сетевой запрос при тестировании UI
        // kotlinx.coroutines.delay(200)

        // Эмитируем сформированный ответ
        emit(response)
    }

}