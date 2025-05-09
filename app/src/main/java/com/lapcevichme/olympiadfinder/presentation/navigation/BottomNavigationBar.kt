package com.lapcevichme.olympiadfinder.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.HOME_GRAPH_ROUTE
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.PROFILE_GRAPH_ROUTE
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_GRAPH_ROUTE
import com.lapcevichme.olympiadfinder.ui.theme.PreviewTheme

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Определяем, какой граф сейчас активен (для подсветки Bottom Nav Item)
    val selectedRoute = when {
        currentRoute?.startsWith(HOME_GRAPH_ROUTE) == true -> HOME_GRAPH_ROUTE
        currentRoute?.startsWith(PROFILE_GRAPH_ROUTE) == true -> PROFILE_GRAPH_ROUTE
        currentRoute?.startsWith(SETTINGS_GRAPH_ROUTE) == true -> SETTINGS_GRAPH_ROUTE
        else -> null // Ни один из известных графов Bottom Nav не активен
    }

    // <-- Вызываем общую Composable для отображения контента -->
    BottomNavigationBarContent(selectedRoute = selectedRoute) { route ->
        // Логика навигации для реального NavController
        if (currentRoute != route) {
            // TODO: Убедитесь, что у вашего NavController есть startDestination
            // и что findStartDestination() работает корректно в вашем графе навигации.
            // В превью это может вызвать ошибку, так как нет реального графа.
            // В реальном приложении эта логика навигации верна.
            navController.navigate(route, navOptions {

                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }

                launchSingleTop = true
                restoreState = true
            })
        }
    }
}

@Composable
private fun BottomNavigationBarContent(
    selectedRoute: String?, // Принимает выбранный маршрут
    onItemClick: (String) -> Unit // Принимает лямбду для обработки клика
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest // Или surface, surfaceContainer, и т.д.
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.name) },
                label = { Text(text = item.name) },
                selected = selectedRoute == item.route,
                onClick = { onItemClick(item.route) }, // Вызываем переданную лямбду при клике
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
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

/*
    ---- PREVIEWS ----
 */

@Composable
fun PreviewBottomNavigationBarSelected(selectedRoute: String) {
    PreviewBottomNavigationBarContent(selectedRoute = selectedRoute)
}

// Обертка для превью, чтобы предоставить PreviewTheme
@Composable
private fun PreviewBottomNavigationBarContent(selectedRoute: String) {
    BottomNavigationBarContent(selectedRoute = selectedRoute, onItemClick = {})
}


// Превью с выбранным элементом "Главная"
@Preview(showBackground = true, name = "Light Theme")
@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Theme"
)
@Composable
fun PreviewBottomNavigationBar_HomeSelected_Light() {
    PreviewTheme {
        PreviewBottomNavigationBarSelected(selectedRoute = HOME_GRAPH_ROUTE)
    }
}

// Превью с выбранным элементом "Профиль"
@Preview(showBackground = true, name = "Light Theme")
@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Theme"
)
@Composable
fun PreviewBottomNavigationBar_ProfileSelected_Light() {
    PreviewTheme {
        PreviewBottomNavigationBarSelected(selectedRoute = PROFILE_GRAPH_ROUTE)
    }
}

// Превью с выбранным элементом "Настройки"
@Preview(showBackground = true, name = "Light Theme")
@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Theme"
)
@Composable
fun PreviewBottomNavigationBar_SettingsSelected_Light() {
    PreviewTheme {
        PreviewBottomNavigationBarSelected(selectedRoute = SETTINGS_GRAPH_ROUTE)
    }
}