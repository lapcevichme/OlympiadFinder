package com.lapcevichme.olympiadfinder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lapcevichme.olympiadfinder.data.local.DEFAULT_PAGE_SIZE
import com.lapcevichme.olympiadfinder.domain.model.Theme
import com.lapcevichme.olympiadfinder.domain.usecases.settings.GetPageSizePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.GetThemePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.SavePageSizePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.SaveThemePreferenceUseCase
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
    private val savePageSizePreferenceUseCase: SavePageSizePreferenceUseCase
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

    fun clearCache() {
        viewModelScope.launch {
            println("Clear cache action triggered in SettingsViewModel")
            // Возможно, нужно вызвать use case или repo метод
        }
    }
}