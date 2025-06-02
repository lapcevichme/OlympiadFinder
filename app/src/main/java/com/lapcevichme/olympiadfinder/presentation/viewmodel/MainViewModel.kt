package com.lapcevichme.olympiadfinder.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lapcevichme.olympiadfinder.domain.model.AppFont
import com.lapcevichme.olympiadfinder.domain.model.Theme
import com.lapcevichme.olympiadfinder.domain.usecases.settings.GetFontPreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.GetThemePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimateThemeChangesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Главный ViewModel приложения, отвечающий за предоставление глобальных настроек UI,
 * таких как текущая тема, настройки анимации темы и выбранный шрифт.
 * Эти настройки собираются из DataStore и предоставляются для MainActivity для применения на уровне всего приложения.
 *
 * @param getThemePreferenceUseCase Use Case для получения текущей темы.
 * @param getAnimateThemeChangesUseCase Use Case для получения настройки анимации смены темы.
 * @param getFontPreferenceUseCase Use Case для получения текущего шрифта приложения.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    getThemePreferenceUseCase: GetThemePreferenceUseCase,
    getAnimateThemeChangesUseCase: GetAnimateThemeChangesUseCase,
    getFontPreferenceUseCase: GetFontPreferenceUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    /**
     * [StateFlow] текущей выбранной темы приложения.
     * Предоставляет тему для MainActivity.
     */
    val theme: StateFlow<Theme> = getThemePreferenceUseCase().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), Theme.SYSTEM
    )

    /**
     * [StateFlow] состояния включения/выключения анимации смены темы.
     * Используется для применения анимации при изменении темы.
     */
    val animateThemeChanges: StateFlow<Boolean> = getAnimateThemeChangesUseCase().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    /**
     * [StateFlow] текущего выбранного шрифта приложения.
     * Используется для применения выбранного шрифта на уровне всего приложения.
     */
    val appFont: StateFlow<AppFont> = getFontPreferenceUseCase().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AppFont.DEFAULT
    )

    init {
        Log.i(TAG, "MainViewModel created with hashcode: ${this.hashCode()}")
        Log.d(TAG, "Initial theme: ${theme.value}")
        Log.d(TAG, "Initial animateThemeChanges: ${animateThemeChanges.value}")
        Log.d(TAG, "Initial font: ${appFont.value}")
    }
}
