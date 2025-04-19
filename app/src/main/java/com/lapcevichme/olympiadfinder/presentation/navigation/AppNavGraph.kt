package com.lapcevichme.olympiadfinder.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
        */

        // 1. Home Graph
        navigation(
            startDestination = HOME_SCREEN,
            route = HOME_GRAPH_ROUTE
        ) {
            composable(route = HOME_SCREEN) { backStackEntry ->
                val graphEntry = remember(backStackEntry) { navController.getBackStackEntry(HOME_GRAPH_ROUTE) }
                val viewModel: OlympiadListViewModel = hiltViewModel(graphEntry)
                OlympiadListScreen(viewModel = viewModel)
            }
        }

        // 2. Profile Graph
        navigation(
            startDestination = PROFILE_SCREEN,
            route = PROFILE_GRAPH_ROUTE
        ) {
            composable(route = PROFILE_SCREEN) { backStackEntry ->
                val graphEntry = remember(backStackEntry) { navController.getBackStackEntry(PROFILE_GRAPH_ROUTE) }
                // ... ProfileScreen ...
                Text("Экран профиля (заглушка)")
            }
        }

        /*
            ---- Settings Graph (Top-level) ----
            Этот граф для всех настроек. Навигация внутри графа управляется NavController SettingsHomeScreen.
        */
        navigation(
            startDestination = SETTINGS_HOME_SCREEN, // Стартовый экран - список категорий
            route = SETTINGS_GRAPH_ROUTE // Маршрут самого графа настроек
        ) {
            // Главный экран настроек
            composable(route = SETTINGS_HOME_SCREEN) { backStackEntry ->
                val graphEntry = remember(backStackEntry) { navController.getBackStackEntry(SETTINGS_GRAPH_ROUTE) }
                val settingsViewModel: SettingsViewModel = hiltViewModel(graphEntry)
                SettingsHomeScreen(navController = navController, viewModel = settingsViewModel) // Передаем NavController
            }

            // Экраны внутри Settings Graph
            composable(route = SETTINGS_ANIMATION_SCREEN) { backStackEntry ->
                val graphEntry = remember(backStackEntry) { navController.getBackStackEntry(SETTINGS_GRAPH_ROUTE) }
                val settingsViewModel: SettingsViewModel = hiltViewModel(graphEntry)
                AnimationSettingsScreen(viewModel = settingsViewModel)
            }

            composable(route = SETTINGS_DATA_SCREEN) { backStackEntry ->
                val graphEntry = remember(backStackEntry) { navController.getBackStackEntry(SETTINGS_GRAPH_ROUTE) }
                val settingsViewModel: SettingsViewModel = hiltViewModel(graphEntry)
                DataSettingsScreen(viewModel = settingsViewModel)
            }

            composable(route = SETTINGS_APPEARANCE_SCREEN) { backStackEntry ->
                val graphEntry = remember(backStackEntry) { navController.getBackStackEntry(SETTINGS_GRAPH_ROUTE) }
                val settingsViewModel: SettingsViewModel = hiltViewModel(graphEntry)
                AppearanceSettingsScreen(viewModel = settingsViewModel)
            }

            composable(route = SETTINGS_DATA_DISPLAY_SCREEN) { backStackEntry ->
                val graphEntry = remember(backStackEntry) { navController.getBackStackEntry(SETTINGS_GRAPH_ROUTE) }
                val settingsViewModel: SettingsViewModel = hiltViewModel(graphEntry)
                DataDisplaySettingsScreen(viewModel = settingsViewModel)
            }


            // TODO: Добавить другие composable для экранов настроек в этот граф
        }

        /*
        ---- OTHER TOP-LEVEL COMPOSABLES (if any, e.g., screens not in Bottom Nav) ----
        */

    }
}