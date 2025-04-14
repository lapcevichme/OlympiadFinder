package com.lapcevichme.olympiadfinder.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    // --- Getting animation states ---
    val animatePageTransitions by viewModel.animatePageTransitions.collectAsState()
    val animateListItems by viewModel.animateListItems.collectAsState()
    val animateThemeChanges by viewModel.animateThemeChanges.collectAsState()

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

        ExpandableCard(title = "Тема приложения") {
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
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Page Size Selection ---

        ExpandableCard(title = "Элементов на странице") {

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
                    Spacer(Modifier.width(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Cache Management ---

        ExpandableCard(title = "Данные") {
            Button(onClick = { viewModel.clearCache() }) {
                Text("Очистить кэш")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Animated Transitions ---

        ExpandableCard(title = "Анимации") {
            Column {

                AnimationToggle(
                    text = "Анимация смены страниц",
                    checked = animatePageTransitions,
                    onCheckedChange = { viewModel.setAnimatePageTransitions(it) }
                )
                AnimationToggle(
                    text = "Анимация элементов списка",
                    checked = animateListItems,
                    onCheckedChange = { viewModel.setAnimateListItems(it) }
                )
                AnimationToggle(
                    text = "Анимация смены темы",
                    checked = animateThemeChanges,
                    onCheckedChange = { viewModel.setAnimateThemeChanges(it) }
                )
            }
        }

        // TODO: Notification toggles, Filter preferences...

        Spacer(modifier = Modifier.height(12.dp))

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

@Composable
private fun AnimationToggle(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 0.dp), // Убрали отступ, т.к. Switch имеет свой
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Текст слева, Switch справа
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun ExpandableCard(
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.clickable { expanded = !expanded }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (expanded) "Свернуть" else "Развернуть"
                    )
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(8.dp)) {
                    content()
                }
            }
        }
    }
}