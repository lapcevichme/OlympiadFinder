package com.lapcevichme.olympiadfinder.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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

        composable(route = "home") { /* HomeScreen() */ }
        composable(route = "profile") { /* ProfileScreen() */ }
        composable(route = "settings") { /* SettingsScreen() */ }

        /*
        ---- THIS IS A LIST OF HomeScreen COMPOSABLES ----
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