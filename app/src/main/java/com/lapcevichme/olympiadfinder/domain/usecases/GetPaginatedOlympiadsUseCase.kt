package com.lapcevichme.olympiadfinder.domain.usecases

import com.lapcevichme.olympiadfinder.data.di.MockOlympiadRepository
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginatedResponse
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPaginatedOlympiadsUseCase @Inject constructor(
    @MockOlympiadRepository private val olympiadRepository: OlympiadRepository
) {
    operator fun invoke(page: Int, pageSize: Int, query: String? = null): Flow<PaginatedResponse<Olympiad>> {
        return olympiadRepository.getOlympiads(page, pageSize, query)
    }
}