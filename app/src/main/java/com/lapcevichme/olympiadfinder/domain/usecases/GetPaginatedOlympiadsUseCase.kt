package com.lapcevichme.olympiadfinder.domain.usecases

import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginatedResponse
import com.lapcevichme.olympiadfinder.domain.model.Resource
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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