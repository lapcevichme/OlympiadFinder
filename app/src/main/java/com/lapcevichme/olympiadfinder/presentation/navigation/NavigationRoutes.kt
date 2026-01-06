package com.lapcevichme.olympiadfinder.presentation.navigation

/**
 * Объект-компаньон, содержащий константы для всех маршрутов и графов навигации в приложении.
 * Используется для обеспечения типобезопасности и предотвращения ошибок при навигации.
 */
object AppDestinations {
    // --- Маршруты графов верхнего уровня (используются в Bottom Navigation Bar) ---

    /**
     * Маршрут для графа "Главная". Содержит экраны, связанные со списком олимпиад.
     */
    const val HOME_GRAPH_ROUTE = "home_graph"

    /**
     * Маршрут для графа "Профиль". Содержит экраны, связанные с профилем пользователя.
     */
    const val PROFILE_GRAPH_ROUTE = "profile_graph"

    /**
     * Маршрут для графа "Настройки". Содержит все экраны настроек приложения.
     */
    const val SETTINGS_GRAPH_ROUTE = "settings_graph"


    // --- Маршруты отдельных экранов (внутри графов) ---

    /**
     * Маршрут для главного экрана списка олимпиад.
     * Является стартовым экраном для [HOME_GRAPH_ROUTE].
     */
    const val HOME_SCREEN = "home_screen"

    /**
     * Маршрут для экрана профиля пользователя.
     * Является стартовым экраном для [PROFILE_GRAPH_ROUTE].
     */
    const val PROFILE_SCREEN = "profile_screen"

    /**
     * Маршрут для главного экрана настроек.
     * Является стартовым экраном для [SETTINGS_GRAPH_ROUTE].
     */
    const val SETTINGS_HOME_SCREEN = "settings_home_screen"

    /**
     * Маршрут для экрана настроек анимации.
     * Находится внутри [SETTINGS_GRAPH_ROUTE].
     */
    const val SETTINGS_ANIMATION_SCREEN = "settings_animation_screen"

    /**
     * Маршрут для экрана настроек данных (например, очистка кэша).
     * Находится внутри [SETTINGS_GRAPH_ROUTE].
     */
    const val SETTINGS_DATA_SCREEN = "settings_data_screen"

    /**
     * Маршрут для экрана настроек внешнего вида (тема, шрифт).
     * Находится внутри [SETTINGS_GRAPH_ROUTE].
     */
    const val SETTINGS_APPEARANCE_SCREEN = "settings_appearance_screen"

    /**
     * Маршрут для экрана настроек отображения данных (например, размер страницы).
     * Находится внутри [SETTINGS_GRAPH_ROUTE].
     */
    const val SETTINGS_DATA_DISPLAY_SCREEN = "settings_data_display_screen"

    // TODO: Добавить другие маршруты экранов
}