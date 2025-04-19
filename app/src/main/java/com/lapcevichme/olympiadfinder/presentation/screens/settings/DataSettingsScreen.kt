package com.lapcevichme.olympiadfinder.presentation.screens.settings

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lapcevichme.olympiadfinder.presentation.viewmodel.SettingsViewModel


@Composable
fun DataSettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    // На этом экране не нужно собирать много состояний, только вызывать clearCache()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Настройки данных", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // --- Cache Management ---
        Text("Управление кэшем", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.clearCache() }) {
            Text("Очистить кэш")
        }

        // TODO: Добавить другие настройки данных, если есть
    }
}
