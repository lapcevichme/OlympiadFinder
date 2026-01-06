package com.lapcevichme.olympiadfinder.domain.usecases

import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginatedResponse
import com.lapcevichme.olympiadfinder.domain.model.Resource
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case для получения пагинированного списка олимпиад с возможностью фильтрации.
 * Обращается к [OlympiadRepository] для получения данных.
 *
 * @param page Номер страницы для запроса.
 * @param pageSize Количество элементов на странице.
 * @param query Поисковый запрос (опционально).
 * @param selectedGrades Список выбранных классов для фильтрации (по умолчанию пустой).
 * @param selectedSubjects Список выбранных ID предметов для фильтрации (по умолчанию пустой).
 */
class GetPaginatedOlympiadsUseCase @Inject constructor(
    private val olympiadRepository: OlympiadRepository
) {
    operator fun invoke(
        page: Int,
        pageSize: Int,
        query: String? = null,
        selectedGrades: List<Int> = emptyList(),
        selectedSubjects: List<Long> = emptyList()
    ): Flow<Resource<PaginatedResponse<Olympiad>>> {
        return olympiadRepository.getPaginatedOlympiads(
            page,
            pageSize,
            query,
            selectedGrades,
            selectedSubjects
        )
    }
}