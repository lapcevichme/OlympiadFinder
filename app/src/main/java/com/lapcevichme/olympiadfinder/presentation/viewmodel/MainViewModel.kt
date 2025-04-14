package com.lapcevichme.olympiadfinder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lapcevichme.olympiadfinder.domain.model.Theme
import com.lapcevichme.olympiadfinder.domain.usecases.settings.GetThemePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimateThemeChangesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getThemePreferenceUseCase: GetThemePreferenceUseCase,
    getAnimateThemeChangesUseCase: GetAnimateThemeChangesUseCase
) : ViewModel() {

    // Expose the theme preference for the MainActivity/Root composable
    val theme: StateFlow<Theme> = getThemePreferenceUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Theme.SYSTEM // Default
        )

    val animateThemeChanges: StateFlow<Boolean> = getAnimateThemeChangesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

}
