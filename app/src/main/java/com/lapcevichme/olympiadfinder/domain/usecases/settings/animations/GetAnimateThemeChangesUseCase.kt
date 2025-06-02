package com.lapcevichme.olympiadfinder.domain.usecases.settings.animations

import com.lapcevichme.olympiadfinder.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case для получения текущей настройки анимации смены темы.
 * Обращается к [SettingsRepository] для получения данных.
 */
class GetAnimateThemeChangesUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<Boolean> = settingsRepository.animateThemeChangesPreference
}