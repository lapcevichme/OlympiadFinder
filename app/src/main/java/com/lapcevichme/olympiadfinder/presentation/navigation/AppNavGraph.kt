package com.lapcevichme.olympiadfinder.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.lapcevichme.olympiadfinder.presentation.screens.OlympiadListScreen
import com.lapcevichme.olympiadfinder.presentation.screens.SettingsScreen
import com.lapcevichme.olympiadfinder.presentation.viewmodel.OlympiadListViewModel
import com.lapcevichme.olympiadfinder.presentation.viewmodel.SettingsViewModel

@Composable
fun AppNavGraph(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = "main_graph",
        Modifier.padding(paddingValues),
    ) {

        /*
            ---- THIS IS A LIST OF BOTTOM NAV ITEMS ----
        */

        navigation(
            startDestination = "home",
            route = "main_graph"
        ) {
            composable(route = "home") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("main_graph")
                }

                val viewModel: OlympiadListViewModel = hiltViewModel(parentEntry)
                OlympiadListScreen(viewModel = viewModel)
            }

            composable(route = "profile") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("main_graph")
                }
                // val profileViewModel: ProfileViewModel = hiltViewModel(parentEntry)
                /* ProfileScreen(viewModel = profileViewModel) */

                //ProfileScreen()
            }

            composable(route = "settings") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("main_graph")
                }
                val settingsViewModel: SettingsViewModel = hiltViewModel(parentEntry)
                SettingsScreen(viewModel = settingsViewModel)
            }
        }


        /*
        ---- THIS IS A LIST OF OlympiadListScreen COMPOSABLES ----
         */

        /*
        ---- THIS IS A LIST OF ProfileScreen COMPOSABLES ----
        */


        /*
        ---- THIS IS A LIST OF SettingsScreen COMPOSABLES ----
        */


        /*
        ---- THIS IS A LIST OF (OTHER) COMPOSABLES ----
        */


    }
}