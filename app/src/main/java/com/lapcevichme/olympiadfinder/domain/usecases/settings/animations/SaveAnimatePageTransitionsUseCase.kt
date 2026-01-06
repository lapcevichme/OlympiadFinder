package com.lapcevichme.olympiadfinder.domain.usecases.settings.animations

import com.lapcevichme.olympiadfinder.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use Case для сохранения настройки анимации переходов между страницами.
 * Обращается к [SettingsRepository] для сохранения данных.
 *
 * @param enabled Новое состояние анимации (true - включено, false - выключено).
 */
class SaveAnimatePageTransitionsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(enabled : Boolean) {
        settingsRepository.setAnimatePageTransitionsPreference(enabled)
    }
}