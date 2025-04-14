package com.lapcevichme.olympiadfinder.domain.repository

import com.lapcevichme.olympiadfinder.domain.model.Theme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val themePreference: Flow<Theme>
    suspend fun setThemePreference(theme: Theme)

    val pageSizePreference: Flow<Int>
    suspend fun setPageSizePreference(size: Int)

    // suspend fun clearCache()
}