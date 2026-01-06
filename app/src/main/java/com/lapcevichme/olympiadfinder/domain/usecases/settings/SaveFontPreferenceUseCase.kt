package com.lapcevichme.olympiadfinder.domain.usecases.settings

import com.lapcevichme.olympiadfinder.domain.model.AppFont
import com.lapcevichme.olympiadfinder.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use Case для сохранения выбранной настройки шрифта приложения.
 * Обращается к [SettingsRepository] для сохранения данных.
 *
 * @param font Шрифт для сохранения.
 */
class SaveFontPreferenceUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(font: AppFont) {
        settingsRepository.setFontPreference(font)
    }
}