package com.lapcevichme.olympiadfinder.data.repository.mock

import com.lapcevichme.olympiadfinder.domain.model.AppError
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginatedResponse
import com.lapcevichme.olympiadfinder.domain.model.PaginationMetadata
import com.lapcevichme.olympiadfinder.domain.model.Resource
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Моковая реализация интерфейса [OlympiadRepository].
 * Используется для предоставления фейковых данных об олимпиадах и предметах
 * для целей тестирования и локальной разработки.
 */
class MockOlympiadRepositoryImpl @Inject constructor() : OlympiadRepository {

    private val allOlympiads = (1..100).map { i ->
        Olympiad(
            id = i.toLong(),
            name = "Олимпиада $i",
            subjects = listOf(
                Subject(0, "Предмет $i"), Subject(1, "Математика"), Subject(2, "Физика")
            ),
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

    /**
     * Возвращает [Flow] с [Resource], содержащим моковый список всех олимпиад.
     * Данные генерируются статически при инициализации мока.
     *
     * @return [Flow] с [Resource], содержащим список [Olympiad].
     */
    override fun getAllOlympiads(): Flow<Resource<List<Olympiad>>> = flow {
        emit(Resource.success(allOlympiads))
    }

    /**
     * Возвращает [Resource] с моковой олимпиадой по указанному [id].
     * Если олимпиада с таким ID не найдена в моковых данных, возвращает [Resource.failure].
     *
     * @param id Уникальный идентификатор олимпиады.
     * @return [Resource], содержащий объект [Olympiad] или ошибку.
     */
    override suspend fun getOlympiadById(id: Long): Resource<Olympiad> {
        val olympiad = allOlympiads.find { it.id == id }
        return if (olympiad != null) {
            Resource.success(olympiad)
        } else {
            Resource.failure(AppError.NotFoundError)
        }
    }

    /**
     * Возвращает [Flow] с [Resource], содержащим моковый пагинированный список олимпиад.
     * Поддерживает фильтрацию по поисковому запросу и классам, а также пагинацию.
     *
     * @param page Номер страницы для запроса.
     * @param pageSize Количество элементов на странице.
     * @param query Поисковый запрос (может быть null).
     * @param selectedGrades Список выбранных классов для фильтрации (может быть пустым).
     * @param selectedSubjects Список выбранных ID предметов для фильтрации (не реализовано в моке).
     * @return [Flow] с [Resource], содержащим [PaginatedResponse].
     */
    override fun getPaginatedOlympiads(
        page: Int,
        pageSize: Int,
        query: String?,
        selectedGrades: List<Int>,
        selectedSubjects: List<Long>
    ): Flow<Resource<PaginatedResponse<Olympiad>>> = flow {
        var currentFilteredList = allOlympiads
        if (!query.isNullOrBlank()) {
            val lowerCaseQuery = query.lowercase()
            currentFilteredList = currentFilteredList.filter { olympiad ->
                olympiad.name.lowercase().contains(lowerCaseQuery) || olympiad.description.orEmpty()
                    .lowercase().contains(lowerCaseQuery) || olympiad.keywords.orEmpty().lowercase()
                    .contains(lowerCaseQuery) || (olympiad.subjects ?: emptyList()).any { subject ->
                    subject.name.lowercase().contains(lowerCaseQuery)
                }
            }
        }
        if (selectedGrades.isNotEmpty()) {
            currentFilteredList = currentFilteredList.filter { olympiad ->
                if (olympiad.minGrade == null || olympiad.maxGrade == null) {
                    false
                } else {
                    selectedGrades.any { selectedGrade ->
                        selectedGrade >= olympiad.minGrade && selectedGrade <= olympiad.maxGrade
                    }
                }
            }
        }
        // Добавление фильтрации по предметам в мок-репозитории
        if (selectedSubjects.isNotEmpty()) {
            currentFilteredList = currentFilteredList.filter { olympiad ->
                olympiad.subjects?.any { subject -> selectedSubjects.contains(subject.id) } == true
            }
        }

        val totalItems = currentFilteredList.size
        val totalPages = (totalItems + pageSize - 1) / pageSize
        val startIndex = (page - 1) * pageSize
        val safeStartIndex = maxOf(0, startIndex)
        val endIndex = minOf(safeStartIndex + pageSize, totalItems)
        val currentPageItems = if (safeStartIndex < endIndex) {
            currentFilteredList.subList(safeStartIndex, endIndex)
        } else {
            emptyList()
        }
        val response = PaginatedResponse(
            items = currentPageItems, meta = PaginationMetadata(
                totalItems = totalItems,
                totalPages = totalPages,
                currentPage = page,
                pageSize = pageSize
            )
        )
        emit(Resource.success(response))
    }

    /**
     * Возвращает [Resource] с моковым списком доступных предметов.
     * Данные генерируются статически.
     *
     * @return [Resource], содержащий список [Subject].
     */
    override suspend fun getAvailableSubjects(): Resource<List<Subject>> {
        return Resource.success(
            listOf(
                Subject(id = 1, name = "Математика"),
                Subject(id = 2, name = "Физика"),
                Subject(id = 3, name = "Информатика")
            )
        )
    }
}
