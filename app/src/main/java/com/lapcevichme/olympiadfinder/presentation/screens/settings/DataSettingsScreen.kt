package com.lapcevichme.olympiadfinder.presentation.screens.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lapcevichme.olympiadfinder.presentation.viewmodel.SettingsViewModel
import com.lapcevichme.olympiadfinder.ui.theme.PreviewTheme


@Composable
fun DataSettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    // На этом экране не нужно собирать много состояний, только вызывать clearCache()

    // UI-логика вынесена в stateless Composable для превью
    DataSettingsScreenContent(
        onClearCacheClick = { viewModel.clearCache() }
    )
}

// Stateless Composable для UI контента DataSettingsScreen
@Composable
private fun DataSettingsScreenContent(
    onClearCacheClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Настройки данных",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Cache Management ---
        Text(
            "Управление кэшем",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onClearCacheClick) {
            Text("Очистить кэш") // Цвет текста кнопки берется из темы Button
        }

        // TODO: Добавить другие настройки данных, если есть
    }
}

// --- PREVIEWS ДЛЯ DataSettingsScreenContent ---
@Preview(showBackground = true, name = "Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Theme"
)
@Composable
fun PreviewDataSettingsScreen() {
    PreviewTheme {
        DataSettingsScreenContent(onClearCacheClick = {})
    }
}
