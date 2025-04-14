package com.lapcevichme.olympiadfinder.domain.usecases.settings

import com.lapcevichme.olympiadfinder.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPageSizePreferenceUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<Int> = settingsRepository.pageSizePreference
}