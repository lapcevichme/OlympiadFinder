package com.lapcevichme.olympiadfinder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lapcevichme.olympiadfinder.data.local.DEFAULT_PAGE_SIZE
import com.lapcevichme.olympiadfinder.domain.model.Theme
import com.lapcevichme.olympiadfinder.domain.usecases.settings.GetPageSizePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.GetThemePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.SavePageSizePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.SaveThemePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimateListItemsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimatePageTransitionsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimateThemeChangesUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.SaveAnimateListItemsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.SaveAnimatePageTransitionsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.SaveAnimateThemeChangesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getThemePreferenceUseCase: GetThemePreferenceUseCase,
    private val saveThemePreferenceUseCase: SaveThemePreferenceUseCase,
    getPageSizePreferenceUseCase: GetPageSizePreferenceUseCase,
    private val savePageSizePreferenceUseCase: SavePageSizePreferenceUseCase,
    // --- Animations Usecases ---
    getAnimatePageTransitionsUseCase: GetAnimatePageTransitionsUseCase,
    private val saveAnimatePageTransitionsUseCase: SaveAnimatePageTransitionsUseCase,
    getAnimateListItemsUseCase: GetAnimateListItemsUseCase,
    private val saveAnimateListItemsUseCase: SaveAnimateListItemsUseCase,
    getAnimateThemeChangesUseCase: GetAnimateThemeChangesUseCase,
    private val saveAnimateThemeChangesUseCase: SaveAnimateThemeChangesUseCase
) : ViewModel() {

    val currentTheme: StateFlow<Theme> = getThemePreferenceUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Theme.SYSTEM
        )

    val currentPageSize: StateFlow<Int> = getPageSizePreferenceUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DEFAULT_PAGE_SIZE
        )

    fun changePageSize(newPageSize: Int) {
        viewModelScope.launch {
            savePageSizePreferenceUseCase(newPageSize)
        }
    }

    fun changeTheme(newTheme: Theme) {
        viewModelScope.launch {
            saveThemePreferenceUseCase(newTheme)
        }
    }

    // --- НОВЫЕ StateFlows и Функции для Анимаций ---
    val animatePageTransitions: StateFlow<Boolean> = getAnimatePageTransitionsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setAnimatePageTransitions(enabled: Boolean) {
        viewModelScope.launch { saveAnimatePageTransitionsUseCase(enabled) }
    }

    val animateListItems: StateFlow<Boolean> = getAnimateListItemsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setAnimateListItems(enabled: Boolean) {
        viewModelScope.launch { saveAnimateListItemsUseCase(enabled) }
    }

    val animateThemeChanges: StateFlow<Boolean> = getAnimateThemeChangesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setAnimateThemeChanges(enabled: Boolean) {
        viewModelScope.launch { saveAnimateThemeChangesUseCase(enabled) }
    }

    fun clearCache() {
        viewModelScope.launch {
            println("Clear cache action triggered in SettingsViewModel")
            // Возможно, нужно вызвать use case или repo метод
        }
    }
}