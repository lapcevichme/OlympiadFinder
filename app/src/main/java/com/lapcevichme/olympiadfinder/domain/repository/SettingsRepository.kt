package com.lapcevichme.olympiadfinder.domain.repository

import com.lapcevichme.olympiadfinder.domain.model.AppFont
import com.lapcevichme.olympiadfinder.domain.model.Theme
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс, определяющий контракт для репозитория, отвечающего за управление
 * пользовательскими настройками приложения (тема, размер страницы, анимации, шрифт).
 */
interface SettingsRepository {
    /**
     * [Flow] текущей выбранной темы приложения.
     */
    val themePreference: Flow<Theme>

    /**
     * Сохраняет выбранную [theme] приложения.
     *
     * @param theme Тема для сохранения.
     */
    suspend fun setThemePreference(theme: Theme)

    /**
     * [Flow] текущего выбранного размера страницы для отображения списков.
     */
    val pageSizePreference: Flow<Int>

    /**
     * Сохраняет выбранный [size] страницы.
     *
     * @param size Размер страницы для сохранения.
     */
    suspend fun setPageSizePreference(size: Int)

    // --- Animated Transitions ---

    /**
     * [Flow] состояния включения/выключения анимации переходов между страницами.
     */
    val animatePageTransitionsPreference: Flow<Boolean>

    /**
     * Сохраняет состояние анимации переходов между страницами.
     *
     * @param enabled `true`, если анимация включена, `false` - выключена.
     */
    suspend fun setAnimatePageTransitionsPreference(enabled: Boolean)

    /**
     * [Flow] состояния включения/выключения анимации элементов списка.
     */
    val animateListItemsPreference: Flow<Boolean>

    /**
     * Сохраняет состояние анимации элементов списка.
     *
     * @param enabled `true`, если анимация включена, `false` - выключена.
     */
    suspend fun setAnimateListItemsPreference(enabled: Boolean)

    /**
     * [Flow] состояния включения/выключения анимации смены темы.
     */
    val animateThemeChangesPreference: Flow<Boolean>

    /**
     * Сохраняет состояние анимации смены темы.
     *
     * @param enabled `true`, если анимация включена, `false` - выключена.
     */
    suspend fun setAnimateThemeChangesPreference(enabled: Boolean)

    // --- Font preferences ---

    /**
     * [Flow] текущего выбранного шрифта приложения.
     */
    val fontPreference: Flow<AppFont>

    /**
     * Сохраняет выбранный [font] приложения.
     *
     * @param font Шрифт для сохранения.
     */
    suspend fun setFontPreference(font: AppFont)

    // suspend fun clearCache()
}