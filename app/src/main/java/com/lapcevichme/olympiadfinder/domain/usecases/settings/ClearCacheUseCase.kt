package com.lapcevichme.olympiadfinder.domain.usecases.settings

import com.lapcevichme.olympiadfinder.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case для очистки кэша приложения.
 *
 * @property settingsRepository Репозиторий настроек, который предоставляет доступ к операции очистки кэша.
 */
class ClearCacheUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    /**
     * Выполняет операцию очистки кэша, вызывая соответствующий метод в [settingsRepository].
     */
    suspend operator fun invoke() {
        settingsRepository.clearCache()
    }
}
