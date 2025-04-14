package com.lapcevichme.olympiadfinder.domain.usecases.settings

import com.lapcevichme.olympiadfinder.domain.repository.SettingsRepository
import javax.inject.Inject

class SavePageSizePreferenceUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(size: Int) {
        settingsRepository.setPageSizePreference(size)
    }
}