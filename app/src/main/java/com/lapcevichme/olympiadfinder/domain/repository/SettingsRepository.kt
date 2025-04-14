package com.lapcevichme.olympiadfinder.domain.repository

import com.lapcevichme.olympiadfinder.domain.model.Theme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val themePreference: Flow<Theme>
    suspend fun setThemePreference(theme: Theme)

    val pageSizePreference: Flow<Int>
    suspend fun setPageSizePreference(size: Int)

    // --- Animated Transitions ---
    val animatePageTransitionsPreference: Flow<Boolean>
    suspend fun setAnimatePageTransitionsPreference(enabled: Boolean)

    val animateListItemsPreference: Flow<Boolean>
    suspend fun setAnimateListItemsPreference(enabled: Boolean)

    val animateThemeChangesPreference: Flow<Boolean>
    suspend fun setAnimateThemeChangesPreference(enabled: Boolean)

    // suspend fun clearCache()
}