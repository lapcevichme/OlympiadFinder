package com.lapcevichme.olympiadfinder.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.HOME_GRAPH_ROUTE
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.PROFILE_GRAPH_ROUTE
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_GRAPH_ROUTE

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Определяем, какой граф сейчас активен (для подсветки Bottom Nav Item)
        val selectedRoute = when {
            currentRoute?.startsWith(HOME_GRAPH_ROUTE) == true -> HOME_GRAPH_ROUTE
            currentRoute?.startsWith(PROFILE_GRAPH_ROUTE) == true -> PROFILE_GRAPH_ROUTE
            currentRoute?.startsWith(SETTINGS_GRAPH_ROUTE) == true -> SETTINGS_GRAPH_ROUTE
            else -> null // Ни один из известных графов Bottom Nav не активен
        }

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.name) },
                label = { Text(text = item.name) },

                selected = selectedRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) { // Навигируем только если уже не в этом графе
                        navController.navigate(item.route, navOptions {

                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        })
                    }
                },
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    }
}

/*
    ---- THIS IS A LIST OF BOTTOM NAV ITEMS ----
    Use GRAPH routes here
 */

val items = listOf(
    BottomNavItem(
        name = "Главная",
        route = HOME_GRAPH_ROUTE,
        icon = Icons.Filled.Home
    ),
    BottomNavItem(
        name = "Профиль",
        route = PROFILE_GRAPH_ROUTE,
        icon = Icons.Filled.Person
    ),
    BottomNavItem(
        name = "Настройки",
        route = SETTINGS_GRAPH_ROUTE,
        icon = Icons.Filled.Settings
    )
)