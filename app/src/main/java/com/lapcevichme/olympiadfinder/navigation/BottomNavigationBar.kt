package com.lapcevichme.olympiadfinder.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomAppBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.name) },
                label = { Text(text = item.name) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer // Цвет индикатора выбранного элемента
                )
            )
        }
    }
}

/*
    ---- THIS IS A LIST OF BOTTOM NAV ITEMS ----
    you can add more items here if you want
    you must add composable in MainActivity.kt !!!
 */

val items = listOf(
    BottomNavItem(
        name = "Главная",
        route = "home",
        icon = Icons.Filled.Home
    ),
    BottomNavItem(
        name = "Профиль",
        route = "profile",
        icon = Icons.Filled.Person
    ),
    BottomNavItem(
        name = "Настройки",
        route = "settings",
        icon = Icons.Filled.Settings
    )
)