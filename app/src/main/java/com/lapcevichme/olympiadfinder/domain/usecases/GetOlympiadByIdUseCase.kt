package com.lapcevichme.olympiadfinder.domain.usecases

import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.Resource
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import javax.inject.Inject

class GetOlympiadByIdUseCase @Inject constructor(
    private val olympiadRepository: OlympiadRepository
) {
    suspend operator fun invoke(id: Long): Resource<Olympiad> {
        return olympiadRepository.getOlympiadById(id)
    }
}