package com.lapcevichme.olympiadfinder.domain.usecases

import com.lapcevichme.olympiadfinder.domain.model.Resource
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import javax.inject.Inject

class GetAvailableSubjectsUseCase @Inject constructor(
    private val repository: OlympiadRepository
) {
    suspend operator fun invoke(): Resource<List<Subject>> {
        return repository.getAvailableSubjects()
    }
}