package com.lapcevichme.olympiadfinder.data.repository.impl

import com.lapcevichme.olympiadfinder.data.local.PreferencesSettingsDataStore
import com.lapcevichme.olympiadfinder.domain.model.Theme
import com.lapcevichme.olympiadfinder.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: PreferencesSettingsDataStore
) : SettingsRepository {

    override val themePreference: Flow<Theme>
        get() = settingsDataStore.themePreference

    override suspend fun setThemePreference(theme: Theme) {
        settingsDataStore.saveThemePreference(theme)
    }

    override val pageSizePreference: Flow<Int>
        get() = settingsDataStore.pageSizePreference

    override suspend fun setPageSizePreference(size: Int) {
        settingsDataStore.savePageSizePreference(size)
    }
}