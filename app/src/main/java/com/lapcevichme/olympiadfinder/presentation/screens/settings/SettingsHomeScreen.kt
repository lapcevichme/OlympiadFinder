package com.lapcevichme.olympiadfinder.presentation.screens.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lapcevichme.olympiadfinder.presentation.components.settings_screen.SettingsCategoryItem
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_ANIMATION_SCREEN
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_APPEARANCE_SCREEN
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_DATA_DISPLAY_SCREEN
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_DATA_SCREEN
import com.lapcevichme.olympiadfinder.presentation.viewmodel.SettingsViewModel
import com.lapcevichme.olympiadfinder.ui.theme.PreviewTheme


@Composable
fun SettingsHomeScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    // UI-логика вынесена в stateless Composable для превью
    SettingsHomeScreenContent(
        onAppearanceClick = { navController.navigate(SETTINGS_APPEARANCE_SCREEN) },
        onDataDisplayClick = { navController.navigate(SETTINGS_DATA_DISPLAY_SCREEN) },
        onAnimationsClick = { navController.navigate(SETTINGS_ANIMATION_SCREEN) },
        onDataClick = { navController.navigate(SETTINGS_DATA_SCREEN) }
        // TODO: Добавить колбэки для других категорий
    )
}

// Stateless Composable для UI контента SettingsHomeScreen
@Composable
private fun SettingsHomeScreenContent(
    onAppearanceClick: () -> Unit,
    onDataDisplayClick: () -> Unit,
    onAnimationsClick: () -> Unit,
    onDataClick: () -> Unit
    // TODO: Добавить колбэки для других категорий
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Настройки",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Раздел "Общие" ---
        Text(
            "Общие",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        SettingsCategoryItem(
            title = "Внешний вид",
            onClick = onAppearanceClick
        )

        // категория "Отображение данных"
        SettingsCategoryItem(
            title = "Отображение данных",
            onClick = onDataDisplayClick
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outline
        ) // Разделитель с цветом

        // --- Список категорий настроек для навигации ---
        Text(
            "Дополнительно",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        SettingsCategoryItem(
            title = "Анимации",
            onClick = onAnimationsClick
        )
        SettingsCategoryItem(
            title = "Данные",
            onClick = onDataClick
        )
        // TODO: Добавить другие категории

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outline
        ) // Разделитель с цветом


        // --- About Section (Оставляем на главном экране) ---
        Text(
            "О приложении",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        // TODO: Add Text items for Version, Links to Privacy Policy, Feedback etc.
        Text(
            "Версия: 1.0.0 (Пример)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// --- PREVIEWS ДЛЯ SettingsHomeScreenContent ---
@Preview(showBackground = true, name = "Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Theme"
)
@Composable
fun PreviewSettingsHomeScreen() {
    PreviewTheme {
        SettingsHomeScreenContent(
            onAppearanceClick = {},
            onDataDisplayClick = {},
            onAnimationsClick = {},
            onDataClick = {}
        )
    }
}
