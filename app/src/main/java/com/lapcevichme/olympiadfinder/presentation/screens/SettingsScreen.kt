package com.lapcevichme.olympiadfinder.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lapcevichme.olympiadfinder.domain.model.Theme
import com.lapcevichme.olympiadfinder.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val currentPageSize by viewModel.currentPageSize.collectAsState()

    // Определим доступные опции для размера страницы
    val pageSizeOptions = listOf(10, 20, 50)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Настройки", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // --- Theme Selection ---
        Text("Тема приложения", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Column(Modifier.selectableGroup()) {
            ThemeRadioButton(
                text = "Светлая",
                selected = currentTheme == Theme.LIGHT,
                onClick = { viewModel.changeTheme(Theme.LIGHT) }
            )
            ThemeRadioButton(
                text = "Темная",
                selected = currentTheme == Theme.DARK,
                onClick = { viewModel.changeTheme(Theme.DARK) }
            )
            ThemeRadioButton(
                text = "Системная",
                selected = currentTheme == Theme.SYSTEM,
                onClick = { viewModel.changeTheme(Theme.SYSTEM) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Page Size Selection ---

        Text("Элементов на странице", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(Modifier.selectableGroup()) {
            pageSizeOptions.forEach { sizeOption ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(48.dp)
                        .selectable(
                            selected = (currentPageSize == sizeOption),
                            onClick = { viewModel.changePageSize(sizeOption) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 8.dp)
                ) {
                    RadioButton(
                        selected = (currentPageSize == sizeOption),
                        onClick = null // null, так как клик обрабатывается на Row
                    )
                    Text(
                        text = sizeOption.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
            }
        }

        // --- Cache Management ---
        Text("Данные", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.clearCache() }) {
            Text("Очистить кэш")
        }

        // TODO: Notification toggles, Filter preferences...

        Spacer(modifier = Modifier.height(24.dp))

        // --- About Section ---
        Text("О приложении", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        // TODO: Add Text items for Version, Links to Privacy Policy, Feedback etc.
        Text("Версия: 1.0.0 (Пример)", style = MaterialTheme.typography.bodyMedium)


    }
}

@Composable
private fun ThemeRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // null recommended for radio buttons when row is selectable
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
