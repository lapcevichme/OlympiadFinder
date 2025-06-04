package com.lapcevichme.olympiadfinder.domain.usecases

import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.Resource
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case для получения списка всех олимпиад.
 * Обращается к [OlympiadRepository] для получения данных.
 */
class GetAllOlympiadsUseCase @Inject constructor(
    private val olympiadRepository: OlympiadRepository
) {
    operator fun invoke(): Flow<Resource<List<Olympiad>>> = olympiadRepository.getAllOlympiads()
}
