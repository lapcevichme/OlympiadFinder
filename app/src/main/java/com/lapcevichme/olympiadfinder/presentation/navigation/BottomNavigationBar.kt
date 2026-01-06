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

/**
 * Composable-функция для отображения Bottom Navigation Bar.
 * Отвечает за навигацию между основными секциями приложения (графами навигации).
 *
 * @param navController [NavController], используемый для выполнения навигационных действий.
 */
@Composable
fun BottomNavigationBar(navController: NavController) {
    // Отслеживает текущий элемент в Back Stack для определения активного маршрута.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Определяет, какой граф сейчас активен, для подсветки соответствующего элемента в Bottom Nav.
    val selectedRoute = when {
        currentRoute?.startsWith(HOME_GRAPH_ROUTE) == true -> HOME_GRAPH_ROUTE
        currentRoute?.startsWith(PROFILE_GRAPH_ROUTE) == true -> PROFILE_GRAPH_ROUTE
        currentRoute?.startsWith(SETTINGS_GRAPH_ROUTE) == true -> SETTINGS_GRAPH_ROUTE
        else -> null // Если ни один из известных графов Bottom Nav не активен
    }

    // Вызываем общую Composable для отображения контента Bottom Navigation Bar.
    BottomNavigationBarContent(selectedRoute = selectedRoute) { route ->
        // Логика навигации для реального NavController:
        // Переход на новый маршрут, если он отличается от текущего.
        if (currentRoute != route) {
            navController.navigate(route, navOptions {
                // popUpTo: Удаляет все экраны до стартовой точки текущего графа,
                // чтобы избежать накопления Back Stack при переключении между табами.
                // saveState = true: Сохраняет состояние экранов, удаленных из Back Stack,
                // чтобы при повторном переходе на этот таб восстановить их состояние.
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // launchSingleTop: Если целевой экран уже находится в верхней части стека,
                // он не будет запущен повторно; вместо этого будет использован существующий экземпляр.
                launchSingleTop = true
                // restoreState: Восстанавливает состояние экрана, если оно было сохранено ранее
                // с помощью saveState = true.
                restoreState = true
            })
        }
    }
}

/**
 * Stateless Composable для отображения визуального контента Bottom Navigation Bar.
 * Принимает выбранный маршрут и колбэк для обработки нажатий на элементы.
 *
 * @param selectedRoute Маршрут текущего выбранного элемента навигации (граф).
 * @param onItemClick Лямбда, вызываемая при нажатии на элемент навигации,
 * передающая маршрут выбранного элемента.
 */
@Composable
private fun BottomNavigationBarContent(
    selectedRoute: String?,
    onItemClick: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.name) },
                label = { Text(text = item.name) },
                selected = selectedRoute == item.route, // Элемент выбран, если его маршрут совпадает с selectedRoute
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