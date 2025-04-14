package com.lapcevichme.olympiadfinder.domain.usecases.settings.animations

import com.lapcevichme.olympiadfinder.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveAnimateListItemsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(enabled : Boolean) {
        settingsRepository.setAnimateListItemsPreference(enabled)
    }
}