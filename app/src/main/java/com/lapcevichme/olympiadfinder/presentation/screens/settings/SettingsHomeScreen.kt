package com.lapcevichme.olympiadfinder.presentation.screens.settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_ANIMATION_SCREEN
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_APPEARANCE_SCREEN
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_DATA_DISPLAY_SCREEN
import com.lapcevichme.olympiadfinder.presentation.navigation.AppDestinations.SETTINGS_DATA_SCREEN
import com.lapcevichme.olympiadfinder.presentation.viewmodel.SettingsViewModel


@Composable
fun SettingsHomeScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Настройки", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // --- Раздел "Общие" ---

        Text("Общие", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        SettingsCategoryItem(
            title = "Внешний вид",
            onClick = { navController.navigate(SETTINGS_APPEARANCE_SCREEN) }
        )

        // категория "Отображение данных"
        SettingsCategoryItem(
            title = "Отображение данных",
            onClick = { navController.navigate(SETTINGS_DATA_DISPLAY_SCREEN) }
        )


        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp)) // Разделитель

        // --- Список категорий настроек для навигации ---
        Text("Дополнительно", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        SettingsCategoryItem(
            title = "Анимации",
            onClick = { navController.navigate(SETTINGS_ANIMATION_SCREEN) }
        )
        SettingsCategoryItem(
            title = "Данные",
            onClick = { navController.navigate(SETTINGS_DATA_SCREEN) }
        )
        // TODO: Добавить другие категории

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))


        // --- About Section (Оставляем на главном экране) ---
        Text("О приложении", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        // TODO: Add Text items for Version, Links to Privacy Policy, Feedback etc.
        Text("Версия: 1.0.0 (Пример)", style = MaterialTheme.typography.bodyMedium)


    }
}

// Вспомогательный компонент для элемента категории настроек (кликабельная строка)
@Composable
fun SettingsCategoryItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
        // можно добавить иконку стрелки > справа
        // Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
    }
}