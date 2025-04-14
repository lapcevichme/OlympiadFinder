package com.lapcevichme.olympiadfinder.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lapcevichme.olympiadfinder.domain.model.Theme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

const val DEFAULT_PAGE_SIZE = 10

@Singleton
class PreferencesSettingsDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        val THEME_PREFERENCE = stringPreferencesKey("theme_preference")
        val PAGE_SIZE_PREFERENCE = intPreferencesKey("page_size_preference")
    }

    // Flow для темы
    val themePreference: Flow<Theme> = context.dataStore.data
        .map { preferences ->
            val themeName = preferences[PreferencesKeys.THEME_PREFERENCE] ?: Theme.SYSTEM.name
            try {
                Theme.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                Theme.SYSTEM
            }
        }

    // Функция сохранения темы
    suspend fun saveThemePreference(theme: Theme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_PREFERENCE] = theme.name
        }
    }

    val pageSizePreference: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.PAGE_SIZE_PREFERENCE] ?: DEFAULT_PAGE_SIZE
        }

    suspend fun savePageSizePreference(pageSize: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PAGE_SIZE_PREFERENCE] = pageSize
        }
    }

    suspend fun clearAppCache() {
        println("App cache clearing requested...")
    }
}