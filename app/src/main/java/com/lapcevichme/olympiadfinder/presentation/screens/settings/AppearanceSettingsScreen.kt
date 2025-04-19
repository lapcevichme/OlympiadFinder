package com.lapcevichme.olympiadfinder.presentation.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lapcevichme.olympiadfinder.domain.model.AppFont
import com.lapcevichme.olympiadfinder.domain.model.Theme
import com.lapcevichme.olympiadfinder.presentation.viewmodel.SettingsViewModel

@Composable
fun AppearanceSettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val currentFont by viewModel.appFont.collectAsState()

    val fontOptions = AppFont.entries.toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Внешний вид", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // --- Theme Selection ---
        Text("Тема приложения", style = MaterialTheme.typography.titleMedium)
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

        // TODO: Добавить другие настройки внешнего вида (шрифты и т.д.)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Шрифт приложения", style = MaterialTheme.typography.titleMedium)
        Column(Modifier.selectableGroup()) {
            fontOptions.forEach { fontOption ->

                ThemeRadioButton(
                    text = when(fontOption) {
                        AppFont.DEFAULT -> "Стандартный"
                        AppFont.SERIF -> "С засечками (Serif)"
                        AppFont.MONOSPACE -> "Моноширинный (Monospace)"
                    },
                    selected = currentFont == fontOption,
                    onClick = { viewModel.changeFont(fontOption) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Настройки шрифта (TODO)", style = MaterialTheme.typography.titleMedium)

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
            onClick = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}