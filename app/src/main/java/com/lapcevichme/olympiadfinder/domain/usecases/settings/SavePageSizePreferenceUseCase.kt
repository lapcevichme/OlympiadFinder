package com.lapcevichme.olympiadfinder.domain.usecases.settings

import com.lapcevichme.olympiadfinder.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use Case для сохранения выбранной настройки размера страницы.
 * Обращается к [SettingsRepository] для сохранения данных.
 *
 * @param size Размер страницы для сохранения.
 */
class SavePageSizePreferenceUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(size: Int) {
        settingsRepository.setPageSizePreference(size)
    }
}