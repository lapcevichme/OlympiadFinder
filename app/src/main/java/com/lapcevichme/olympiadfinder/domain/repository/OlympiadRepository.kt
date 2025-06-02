package com.lapcevichme.olympiadfinder.domain.repository

import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginatedResponse
import com.lapcevichme.olympiadfinder.domain.model.Resource
import com.lapcevichme.olympiadfinder.domain.model.Subject
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс, определяющий контракт для репозитория, отвечающего за получение
 * данных об олимпиадах и связанных сущностях (предметах).
 */
interface OlympiadRepository {
    /**
     * Получает [Flow] с [Result], содержащим список всех олимпиад.
     */
    fun getAllOlympiads(): Flow<Result<List<Olympiad>>>

    /**
     * Получает [Resource], содержащий олимпиаду по ее уникальному [id].
     *
     * @param id Уникальный идентификатор олимпиады.
     */
    suspend fun getOlympiadById(id: Long): Resource<Olympiad>

    /**
     * Получает [Flow] с [Resource], содержащим пагинированный список олимпиад.
     *
     * @param page Номер страницы для запроса.
     * @param pageSize Количество элементов на странице.
     * @param query Поисковый запрос (опционально).
     * @param selectedGrades Список выбранных классов для фильтрации (опционально).
     * @param selectedSubjects Список выбранных ID предметов для фильтрации (опционально).
     */
    fun getPaginatedOlympiads(
        page: Int,
        pageSize: Int,
        query: String?,
        selectedGrades: List<Int>,
        selectedSubjects: List<Long>
    ): Flow<Resource<PaginatedResponse<Olympiad>>>

    /**
     * Получает [Resource], содержащий список доступных предметов.
     */
    suspend fun getAvailableSubjects(): Resource<List<Subject>>
}