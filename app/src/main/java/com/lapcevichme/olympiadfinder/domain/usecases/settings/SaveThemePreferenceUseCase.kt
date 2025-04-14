package com.lapcevichme.olympiadfinder.domain.usecases.settings

import com.lapcevichme.olympiadfinder.domain.model.Theme
import com.lapcevichme.olympiadfinder.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveThemePreferenceUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(theme: Theme) {
        settingsRepository.setThemePreference(theme)
    }
}
