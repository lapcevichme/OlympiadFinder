package com.lapcevichme.olympiadfinder.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lapcevichme.olympiadfinder.presentation.screens.OlympiadListScreen

@Composable
fun AppNavGraph(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = "home",
        Modifier.padding(paddingValues)
    ) {
        /*
        ---- THIS IS A LIST OF BOTTOM NAV ITEMS ----
        */

        composable(route = "home") { OlympiadListScreen() }
        composable(route = "profile") { /* ProfileScreen() */ }
        composable(route = "settings") { /* SettingsScreen() */ }

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