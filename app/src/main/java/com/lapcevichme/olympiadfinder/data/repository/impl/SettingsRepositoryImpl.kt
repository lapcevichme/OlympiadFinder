package com.lapcevichme.olympiadfinder.data.repository.impl

import com.lapcevichme.olympiadfinder.data.local.PreferencesSettingsDataStore
import com.lapcevichme.olympiadfinder.domain.model.AppFont
import com.lapcevichme.olympiadfinder.domain.model.Theme
import com.lapcevichme.olympiadfinder.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация интерфейса [SettingsRepository], отвечающая за управление пользовательскими
 * предпочтениями, такими как тема приложения, размер страницы и настройки анимации,
 * используя [PreferencesSettingsDataStore].
 *
 * @property settingsDataStore DataStore для хранения настроек приложения.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: PreferencesSettingsDataStore
) : SettingsRepository {

    /**
     * [Flow] текущей выбранной темы приложения.
     * Собирается из [PreferencesSettingsDataStore].
     */
    override val themePreference: Flow<Theme>
        get() = settingsDataStore.themePreference

    /**
     * Сохраняет выбранную тему приложения в [PreferencesSettingsDataStore].
     *
     * @param theme Тема для сохранения.
     */
    override suspend fun setThemePreference(theme: Theme) {
        settingsDataStore.saveThemePreference(theme)
    }

    /**
     * [Flow] текущего выбранного размера страницы для отображения списков.
     * Собирается из [PreferencesSettingsDataStore].
     */
    override val pageSizePreference: Flow<Int>
        get() = settingsDataStore.pageSizePreference

    /**
     * Сохраняет выбранный размер страницы в [PreferencesSettingsDataStore].
     *
     * @param size Размер страницы для сохранения.
     */
    override suspend fun setPageSizePreference(size: Int) {
        settingsDataStore.savePageSizePreference(size)
    }

    // --- Animated Transitions ---

    /**
     * [Flow] состояния включения/выключения анимации переходов между страницами.
     * Собирается из [PreferencesSettingsDataStore].
     */
    override val animatePageTransitionsPreference: Flow<Boolean>
        get() = settingsDataStore.animatePageTransitionsPreference

    /**
     * Сохраняет состояние включения/выключения анимации переходов между страницами
     * в [PreferencesSettingsDataStore].
     *
     * @param enabled Состояние анимации для сохранения.
     */
    override suspend fun setAnimatePageTransitionsPreference(enabled: Boolean) {
        settingsDataStore.saveAnimatePageTransitionsPreference(enabled)
    }

    /**
     * [Flow] состояния включения/выключения анимации элементов списка.
     * Собирается из [PreferencesSettingsDataStore].
     */
    override val animateListItemsPreference: Flow<Boolean>
        get() = settingsDataStore.animateListItemsPreference

    /**
     * Сохраняет состояние включения/выключения анимации элементов списка
     * в [PreferencesSettingsDataStore].
     *
     * @param enabled Состояние анимации для сохранения.
     */
    override suspend fun setAnimateListItemsPreference(enabled: Boolean) {
        settingsDataStore.saveAnimateListItemsPreference(enabled)
    }

    /**
     * [Flow] состояния включения/выключения анимации смены темы.
     * Собирается из [PreferencesSettingsDataStore].
     */
    override val animateThemeChangesPreference: Flow<Boolean>
        get() = settingsDataStore.animateThemeChangesPreference

    /**
     * Сохраняет состояние включения/выключения анимации смены темы
     * в [PreferencesSettingsDataStore].
     *
     * @param enabled Состояние анимации для сохранения.
     */
    override suspend fun setAnimateThemeChangesPreference(enabled: Boolean) {
        settingsDataStore.saveAnimateThemeChangesPreference(enabled)
    }

    /**
     * [Flow] текущего выбранного шрифта приложения.
     * Собирается из [PreferencesSettingsDataStore].
     */
    override val fontPreference: Flow<AppFont>
        get() = settingsDataStore.fontPreference

    /**
     * Сохраняет выбранный шрифт приложения в [PreferencesSettingsDataStore].
     *
     * @param font Шрифт для сохранения.
     */
    override suspend fun setFontPreference(font: AppFont) {
        settingsDataStore.saveFontPreference(font)
    }
}