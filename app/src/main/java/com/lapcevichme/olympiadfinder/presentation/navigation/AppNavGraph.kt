package com.lapcevichme.olympiadfinder.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.HOME_GRAPH_ROUTE
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.HOME_SCREEN
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.PROFILE_GRAPH_ROUTE
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.PROFILE_SCREEN
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_ANIMATION_SCREEN
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_APPEARANCE_SCREEN
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_DATA_DISPLAY_SCREEN
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_DATA_SCREEN
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_GRAPH_ROUTE
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_HOME_SCREEN
import com.lapcevichme.olympiadfinder.presentation.screens.OlympiadListScreen
import com.lapcevichme.olympiadfinder.presentation.screens.settings.AnimationSettingsScreen
import com.lapcevichme.olympiadfinder.presentation.screens.settings.AppearanceSettingsScreen
import com.lapcevichme.olympiadfinder.presentation.screens.settings.DataDisplaySettingsScreen
import com.lapcevichme.olympiadfinder.presentation.screens.settings.DataSettingsScreen
import com.lapcevichme.olympiadfinder.presentation.screens.settings.SettingsHomeScreen
import com.lapcevichme.olympiadfinder.presentation.viewmodel.OlympiadListViewModel
import com.lapcevichme.olympiadfinder.presentation.viewmodel.SettingsViewModel

/**
 * Определяет граф навигации приложения с помощью [NavHost].
 * Содержит все экраны и вложенные графы, доступные для навигации.
 *
 * @param navController [NavHostController], используемый для управления навигацией.
 * @param paddingValues [PaddingValues], применяемые к содержимому [NavHost] для учета
 * таких элементов, как BottomNavigationBar.
 */
@Composable
fun AppNavGraph(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = HOME_GRAPH_ROUTE,
        modifier = Modifier.padding(paddingValues)
    ) {

        /*
            ---- BOTTOM NAV GRAPHS (Top-level) ----
            Каждый граф соответствует пункту в BottomNavigationBar
            и является корневым для своей секции навигации.
        */

        // 1. Home Graph: Содержит экраны, связанные с главной страницей (список олимпиад).
        navigation(
            startDestination = HOME_SCREEN, // Стартовый экран этого графа
            route = HOME_GRAPH_ROUTE // Уникальный маршрут для этого графа
        ) {
            composable(route = HOME_SCREEN) {
                // ViewModel для OlympiadListScreen инжектится с помощью hiltViewModel(),
                // что означает, что его жизненный цикл привязан к этому composable.
                val viewModel: OlympiadListViewModel = hiltViewModel()
                OlympiadListScreen(viewModel = viewModel)
            }
            // TODO: Добавить другие экраны, принадлежащие Home Graph (например, экран деталей олимпиады)
        }

        // 2. Profile Graph: Содержит экраны, связанные с профилем пользователя.
        navigation(
            startDestination = PROFILE_SCREEN, // Стартовый экран этого графа
            route = PROFILE_GRAPH_ROUTE // Уникальный маршрут для этого графа
        ) {
            composable(route = PROFILE_SCREEN) {
                // Пример заглушки для экрана профиля. ViewModel может быть scoped to Activity
                // или к этому composable в зависимости от требований.
                // val profileViewModel: ProfileViewModel = hiltViewModel()
                Text("Экран профиля (заглушка)")
            }
            // TODO: Добавить другие экраны, принадлежащие Profile Graph
        }

        /*
            ---- Settings Graph (Top-level) ----
            Граф настроек, содержащий различные подэкраны настроек.
            ViewModel'ы на этих экранах обычно могут быть scoped to Activity
            для сохранения состояния между переходами внутри графа настроек.
        */
        navigation(
            startDestination = SETTINGS_HOME_SCREEN, // Стартовый экран графа настроек
            route = SETTINGS_GRAPH_ROUTE // Уникальный маршрут для графа настроек
        ) {
            // Главный экран настроек (список категорий настроек)
            composable(route = SETTINGS_HOME_SCREEN) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                SettingsHomeScreen(navController = navController, viewModel = settingsViewModel)
            }

            // Экраны внутри Settings Graph:
            // Экран настроек анимации
            composable(route = SETTINGS_ANIMATION_SCREEN) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                AnimationSettingsScreen(viewModel = settingsViewModel)
            }

            // Экран настроек данных (например, очистка кэша)
            composable(route = SETTINGS_DATA_SCREEN) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                DataSettingsScreen(viewModel = settingsViewModel)
            }

            // Экран настроек внешнего вида (тема, шрифт)
            composable(route = SETTINGS_APPEARANCE_SCREEN) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                AppearanceSettingsScreen(viewModel = settingsViewModel)
            }

            // Экран настроек отображения данных (например, размер страницы)
            composable(route = SETTINGS_DATA_DISPLAY_SCREEN) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                DataDisplaySettingsScreen(viewModel = settingsViewModel)
            }

            // TODO: Добавить другие composable для экранов настроек в этот граф
        }

        /*
        ---- OTHER TOP-LEVEL COMPOSABLES (if any, e.g., screens not in Bottom Nav) ----
        Дополнительные экраны, которые не являются частью графов Bottom Navigation Bar,
        но доступны напрямую из NavHost.
        */

    }
}