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
            composable(route = HOME_SCREEN) {
                val viewModel: OlympiadListViewModel = hiltViewModel()
                OlympiadListScreen(viewModel = viewModel)
            }
            // TODO: Другие экраны Home Graph
        }

        // 2. Profile Graph
        navigation(
            startDestination = PROFILE_SCREEN,
            route = PROFILE_GRAPH_ROUTE
        ) {
            composable(route = PROFILE_SCREEN) {
                // ViewModel scoped to Activity
                // val profileViewModel: ProfileViewModel = hiltViewModel()
                Text("Экран профиля (заглушка)")
            }
            // TODO: Другие экраны Profile Graph
        }

        /*
            ---- Settings Graph (Top-level) ----
            ViewModel'ы будут scoped to Activity.
        */
        navigation(
            startDestination = SETTINGS_HOME_SCREEN,
            route = SETTINGS_GRAPH_ROUTE
        ) {
            // Главный экран настроек (список категорий)
            composable(route = SETTINGS_HOME_SCREEN) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                SettingsHomeScreen(navController = navController, viewModel = settingsViewModel)
            }

            // Экраны внутри Settings Graph
            composable(route = SETTINGS_ANIMATION_SCREEN) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                AnimationSettingsScreen(viewModel = settingsViewModel)
            }

            composable(route = SETTINGS_DATA_SCREEN) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                DataSettingsScreen(viewModel = settingsViewModel)
            }

            composable(route = SETTINGS_APPEARANCE_SCREEN) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                AppearanceSettingsScreen(viewModel = settingsViewModel)
            }

            composable(route = SETTINGS_DATA_DISPLAY_SCREEN) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                DataDisplaySettingsScreen(viewModel = settingsViewModel)
            }


            // TODO: Добавить другие composable для экранов настроек в этот граф
        }

        /*
        ---- OTHER TOP-LEVEL COMPOSABLES (if any, e.g., screens not in Bottom Nav) ----
        */

    }
}