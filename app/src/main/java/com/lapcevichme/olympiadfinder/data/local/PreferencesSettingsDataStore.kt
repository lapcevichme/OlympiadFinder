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
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Класс для взаимодействия с Jetpack DataStore для хранения и получения пользовательских настроек приложения.
 * Включает настройки темы, размера страницы, состояния анимаций и шрифта.
 */
@Singleton
class PreferencesSettingsDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private const val TAG = "PreferencesDataStore"
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        /**
         * Значение по умолчанию для размера страницы, используемое при первом запуске или отсутствии настройки.
         */
        const val DEFAULT_PAGE_SIZE = 10
    }

    private object PreferencesKeys {
        val THEME_PREFERENCE = stringPreferencesKey("theme_preference")
        val PAGE_SIZE_PREFERENCE = intPreferencesKey("page_size_preference")

        // --- Ключи для настроек анимаций ---
        val ANIMATE_PAGE_TRANSITIONS = booleanPreferencesKey("animate_page_transitions")
        val ANIMATE_LIST_ITEMS = booleanPreferencesKey("animate_list_items")
        val ANIMATE_THEME_CHANGES = booleanPreferencesKey("animate_theme_changes")

        // --- Ключ для настройки шрифта ---
        val FONT_PREFERENCE_KEY = stringPreferencesKey("app_font")
    }

    /**
     * [Flow] текущей выбранной темы приложения. По умолчанию используется системная тема.
     */
    val themePreference: Flow<Theme> = context.dataStore.data
        .map { preferences ->
            val themeName = preferences[PreferencesKeys.THEME_PREFERENCE] ?: Theme.SYSTEM.name
            try {
                Theme.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Ошибка при парсинге темы: $themeName", e)
                Theme.SYSTEM
            }
        }

    /**
     * Сохраняет выбранную [theme] приложения в DataStore.
     *
     * @param theme Тема для сохранения.
     */
    suspend fun saveThemePreference(theme: Theme) {
        Log.d(TAG, "Сохранение темы: ${theme.name}")
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_PREFERENCE] = theme.name
        }
    }

    /**
     * [Flow] текущего выбранного размера страницы для отображения списков.
     * По умолчанию используется [DEFAULT_PAGE_SIZE].
     */
    val pageSizePreference: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.PAGE_SIZE_PREFERENCE] ?: DEFAULT_PAGE_SIZE
        }

    /**
     * Сохраняет выбранный [pageSize] для отображения списков в DataStore.
     *
     * @param pageSize Размер страницы для сохранения.
     */
    suspend fun savePageSizePreference(pageSize: Int) {
        Log.d(TAG, "Сохранение размера страницы: $pageSize")
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PAGE_SIZE_PREFERENCE] = pageSize
        }
    }

    // --- Flows & Savers для настроек анимаций ---

    /**
     * [Flow] состояния анимации переходов между страницами. По умолчанию включено.
     */
    val animatePageTransitionsPreference: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.ANIMATE_PAGE_TRANSITIONS] ?: true }

    /**
     * Сохраняет состояние анимации переходов между страницами в DataStore.
     *
     * @param enabled `true`, если анимация включена, `false` - выключена.
     */
    suspend fun saveAnimatePageTransitionsPreference(enabled: Boolean) {
        Log.d(TAG, "Сохранение состояния анимации переходов: $enabled")
        context.dataStore.edit { it[PreferencesKeys.ANIMATE_PAGE_TRANSITIONS] = enabled }
    }

    /**
     * [Flow] состояния анимации элементов списка. По умолчанию включено.
     */
    val animateListItemsPreference: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.ANIMATE_LIST_ITEMS] ?: true }

    /**
     * Сохраняет состояние анимации элементов списка в DataStore.
     *
     * @param enabled `true`, если анимация включена, `false` - выключена.
     */
    suspend fun saveAnimateListItemsPreference(enabled: Boolean) {
        Log.d(TAG, "Сохранение состояния анимации элементов списка: $enabled")
        context.dataStore.edit { it[PreferencesKeys.ANIMATE_LIST_ITEMS] = enabled }
    }

    /**
     * [Flow] состояния анимации смены темы. По умолчанию включено.
     */
    val animateThemeChangesPreference: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.ANIMATE_THEME_CHANGES] ?: true }

    /**
     * Сохраняет состояние анимации смены темы в DataStore.
     *
     * @param enabled `true`, если анимация включена, `false` - выключена.
     */
    suspend fun saveAnimateThemeChangesPreference(enabled: Boolean) {
        Log.d(TAG, "Сохранение состояния анимации темы: $enabled")
        context.dataStore.edit { it[PreferencesKeys.ANIMATE_THEME_CHANGES] = enabled }
    }

    // --- Flows & Savers для настроек шрифта ---

    /**
     * [Flow] текущего выбранного шрифта приложения. По умолчанию используется шрифт по умолчанию,
     * определенный в [AppFont.fromKey].
     */
    val fontPreference: Flow<AppFont> = context.dataStore.data
        .map { preferences ->
            AppFont.fromKey(preferences[PreferencesKeys.FONT_PREFERENCE_KEY])
        }

    /**
     * Сохраняет выбранный [font] приложения в DataStore.
     *
     * @param font Шрифт для сохранения.
     */
    suspend fun saveFontPreference(font: AppFont) {
        Log.d(TAG, "Сохранение шрифта: ${font.key}")
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_PREFERENCE_KEY] = font.key
        }
    }

    /**
     * Функция для очистки кэша приложения. В текущей реализации только логирует запрос.
     * TODO: Реализовать фактическую очистку кэша.
     */
    suspend fun clearAppCache() {
        Log.i(TAG, "Запрос на очистку кэша приложения...")
        // TODO: Реализовать логику очистки кэша
    }
}