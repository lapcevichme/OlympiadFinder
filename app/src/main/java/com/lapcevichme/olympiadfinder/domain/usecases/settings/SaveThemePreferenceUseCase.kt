package com.lapcevichme.olympiadfinder.domain.usecases.settings

import com.lapcevichme.olympiadfinder.domain.model.Theme
import com.lapcevichme.olympiadfinder.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use Case для сохранения выбранной настройки темы приложения.
 * Обращается к [SettingsRepository] для сохранения данных.
 *
 * @param theme Тема для сохранения.
 */
class SaveThemePreferenceUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(theme: Theme) {
        settingsRepository.setThemePreference(theme)
    }
}
