package com.lapcevichme.olympiadfinder.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lapcevichme.olympiadfinder.domain.model.AppFont
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

        // --- Animation preferences ---
        val ANIMATE_PAGE_TRANSITIONS = booleanPreferencesKey("animate_page_transitions")
        val ANIMATE_LIST_ITEMS = booleanPreferencesKey("animate_list_items")
        val ANIMATE_THEME_CHANGES = booleanPreferencesKey("animate_theme_changes")

        // --- Font preferences ---
        val FONT_PREFERENCE_KEY = stringPreferencesKey("app_font")
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

    // --- Flows & Savers for animation preferences ---
    val animatePageTransitionsPreference: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.ANIMATE_PAGE_TRANSITIONS] ?: true } // Включены по умолчанию

    suspend fun saveAnimatePageTransitionsPreference(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ANIMATE_PAGE_TRANSITIONS] = enabled }
    }

    val animateListItemsPreference: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.ANIMATE_LIST_ITEMS] ?: true }

    suspend fun saveAnimateListItemsPreference(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ANIMATE_LIST_ITEMS] = enabled }
    }

    val animateThemeChangesPreference: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.ANIMATE_THEME_CHANGES] ?: true }

    suspend fun saveAnimateThemeChangesPreference(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ANIMATE_THEME_CHANGES] = enabled }
    }

    // --- Flows & Savers for font preferences ---
    val fontPreference: Flow<AppFont> = context.dataStore.data
        .map { preferences ->
            // Читаем строку по ключу, преобразуем ее в AppFont с помощью fromKey
            AppFont.fromKey(preferences[PreferencesKeys.FONT_PREFERENCE_KEY])
        }

    suspend fun saveFontPreference(font: AppFont) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_PREFERENCE_KEY] = font.key
        }
    }

    suspend fun clearAppCache() {
        println("App cache clearing requested...")
    }
}