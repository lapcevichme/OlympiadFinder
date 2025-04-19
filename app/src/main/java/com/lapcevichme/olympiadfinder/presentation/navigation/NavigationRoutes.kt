package com.lapcevichme.olympiadfinder.presentation.navigation

object AppDestinations {
    // Маршруты для верхнеуровневых графов Bottom Nav
    const val HOME_GRAPH_ROUTE = "home_graph"
    const val PROFILE_GRAPH_ROUTE = "profile_graph"
    const val SETTINGS_GRAPH_ROUTE = "settings_graph"

    // Маршруты для экранов внутри графов (стартовые и внутренние)
    const val HOME_SCREEN = "home_screen" // Экран внутри Home Graph
    const val PROFILE_SCREEN = "profile_screen" // Экран внутри Profile Graph

    // Маршруты для экранов настроек
    const val SETTINGS_HOME_SCREEN = "settings_home_screen" // Экран внутри Settings Graph (стартовый)
    const val SETTINGS_ANIMATION_SCREEN = "settings_animation_screen" // Экран внутри Settings Graph
    const val SETTINGS_DATA_SCREEN = "settings_data_screen" // Экран внутри Settings Graph
    const val SETTINGS_APPEARANCE_SCREEN = "settings_appearance_screen" // Экран настроек внешнего вида
    const val SETTINGS_DATA_DISPLAY_SCREEN = "settings_data_display_screen" // Экран настроек отображения данных

    // TODO: Добавить другие маршруты экранов настроек, если будут
}