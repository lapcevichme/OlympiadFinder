package com.lapcevichme.olympiadfinder.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lapcevichme.olympiadfinder.data.local.PreferencesSettingsDataStore.Companion.DEFAULT_PAGE_SIZE
import com.lapcevichme.olympiadfinder.domain.model.AppFont
import com.lapcevichme.olympiadfinder.domain.model.Theme
import com.lapcevichme.olympiadfinder.domain.usecases.settings.ClearCacheUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.GetFontPreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.GetPageSizePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.GetThemePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.SaveFontPreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.SavePageSizePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.SaveThemePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimateListItemsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimatePageTransitionsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimateThemeChangesUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.SaveAnimateListItemsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.SaveAnimatePageTransitionsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.SaveAnimateThemeChangesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    // --- Theme Use Cases ---
    getThemePreferenceUseCase: GetThemePreferenceUseCase,
    private val saveThemePreferenceUseCase: SaveThemePreferenceUseCase,
    // --- Page Size Use Cases ---
    getPageSizePreferenceUseCase: GetPageSizePreferenceUseCase,
    private val savePageSizePreferenceUseCase: SavePageSizePreferenceUseCase,
    // --- Animations Use Cases ---
    getAnimatePageTransitionsUseCase: GetAnimatePageTransitionsUseCase,
    private val saveAnimatePageTransitionsUseCase: SaveAnimatePageTransitionsUseCase,
    getAnimateListItemsUseCase: GetAnimateListItemsUseCase,
    private val saveAnimateListItemsUseCase: SaveAnimateListItemsUseCase,
    getAnimateThemeChangesUseCase: GetAnimateThemeChangesUseCase,
    private val saveAnimateThemeChangesUseCase: SaveAnimateThemeChangesUseCase,
    // --- Font Use Cases ---
    getFontPreferenceUseCase: GetFontPreferenceUseCase,
    private val saveFontPreferenceUseCase: SaveFontPreferenceUseCase,
    // --- Cache Use Case ---
    private val clearCacheUseCase: ClearCacheUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "SettingsViewModel"
    }

    // --- StateFlows для настроек ---
    val currentTheme: StateFlow<Theme> = getThemePreferenceUseCase().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), Theme.SYSTEM
    )
    val currentPageSize: StateFlow<Int> = getPageSizePreferenceUseCase().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), DEFAULT_PAGE_SIZE
    )
    val animatePageTransitions: StateFlow<Boolean> = getAnimatePageTransitionsUseCase().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )
    val animateListItems: StateFlow<Boolean> = getAnimateListItemsUseCase().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )
    val animateThemeChanges: StateFlow<Boolean> = getAnimateThemeChangesUseCase().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )
    val appFont: StateFlow<AppFont> = getFontPreferenceUseCase().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AppFont.DEFAULT
    )

    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents: SharedFlow<String> = _uiEvents.asSharedFlow()

    init {
        Log.i(TAG, "SettingsViewModel created with hashcode: ${this.hashCode()}")
        Log.d(TAG, "Initial theme: ${currentTheme.value}")
        Log.d(TAG, "Initial page size: ${currentPageSize.value}")
        Log.d(TAG, "Initial animatePageTransitions: ${animatePageTransitions.value}")
        Log.d(TAG, "Initial animateListItems: ${animateListItems.value}")
        Log.d(TAG, "Initial animateThemeChanges: ${animateThemeChanges.value}")
        Log.d(TAG, "Initial font: ${appFont.value}")
    }

    /**
     * Изменяет и сохраняет предпочтение размера страницы.
     * @param newPageSize Новый размер страницы для сохранения.
     */
    fun changePageSize(newPageSize: Int) {
        Log.d(TAG, "Attempting to change page size to: $newPageSize")
        viewModelScope.launch {
            savePageSizePreferenceUseCase(newPageSize)
            Log.i(TAG, "Page size changed to: $newPageSize")
        }
    }

    /**
     * Изменяет и сохраняет предпочтение темы приложения.
     * @param newTheme Новая тема для сохранения.
     */
    fun changeTheme(newTheme: Theme) {
        Log.d(TAG, "Attempting to change theme to: $newTheme")
        viewModelScope.launch {
            saveThemePreferenceUseCase(newTheme)
            Log.i(TAG, "Theme changed to: $newTheme")
        }
    }

    /**
     * Изменяет и сохраняет предпочтение анимации переходов между страницами.
     * @param enabled Состояние включения/выключения анимации.
     */
    fun setAnimatePageTransitions(enabled: Boolean) {
        Log.d(TAG, "Attempting to set animatePageTransitions to: $enabled")
        viewModelScope.launch {
            saveAnimatePageTransitionsUseCase(enabled)
            Log.i(TAG, "animatePageTransitions set to: $enabled")
        }
    }

    /**
     * Изменяет и сохраняет предпочтение анимации элементов списка.
     * @param enabled Состояние включения/выключения анимации.
     */
    fun setAnimateListItems(enabled: Boolean) {
        Log.d(TAG, "Attempting to set animateListItems to: $enabled")
        viewModelScope.launch {
            saveAnimateListItemsUseCase(enabled)
            Log.i(TAG, "animateListItems set to: $enabled")
        }
    }

    /**
     * Изменяет и сохраняет предпочтение анимации смены темы.
     * @param enabled Состояние включения/выключения анимации.
     */
    fun setAnimateThemeChanges(enabled: Boolean) {
        Log.d(TAG, "Attempting to set animateThemeChanges to: $enabled")
        viewModelScope.launch {
            saveAnimateThemeChangesUseCase(enabled)
            Log.i(TAG, "animateThemeChanges set to: $enabled")
        }
    }

    /**
     * Изменяет и сохраняет предпочтение шрифта приложения.
     * @param font Новый шрифт для сохранения.
     */
    fun changeFont(font: AppFont) {
        Log.d(TAG, "Attempting to change font to: $font")
        viewModelScope.launch {
            saveFontPreferenceUseCase(font)
            Log.i(TAG, "Font changed to: $font")
        }
    }


    /**
     * Инициирует действие по очистке кэша приложения.
     * Вызывает [ClearCacheUseCase] и отправляет событие на UI для показа Snackbar.
     */
    fun clearCache() {
        Log.d(TAG, "Clear cache action triggered in SettingsViewModel")
        viewModelScope.launch {
            try {
                clearCacheUseCase()
                _uiEvents.emit("Кэш успешно очищен")
                Log.i(TAG, "Cache clear operation successful.")
            } catch (e: Exception) {
                Log.e(TAG, "Cache clear operation failed.", e)
                _uiEvents.emit("Ошибка при очистке кэша")
            }
        }
    }
}