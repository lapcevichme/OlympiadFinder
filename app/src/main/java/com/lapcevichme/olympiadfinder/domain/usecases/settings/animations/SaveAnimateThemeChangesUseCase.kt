package com.lapcevichme.olympiadfinder.domain.usecases.settings.animations

import com.lapcevichme.olympiadfinder.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use Case для сохранения настройки анимации смены темы.
 * Обращается к [SettingsRepository] для сохранения данных.
 *
 * @param enabled Новое состояние анимации (true - включено, false - выключено).
 */
class SaveAnimateThemeChangesUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(enabled : Boolean) {
        settingsRepository.setAnimateThemeChangesPreference(enabled)
    }
}