package com.lapcevichme.olympiadfinder.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lapcevichme.olympiadfinder.presentation.viewmodel.SettingsViewModel


@Composable
fun AnimationSettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val animatePageTransitions by viewModel.animatePageTransitions.collectAsState()
    val animateListItems by viewModel.animateListItems.collectAsState()
    val animateThemeChanges by viewModel.animateThemeChanges.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Настройки анимации", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // --- Animation Toggles ---
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

        // TODO: Добавить другие настройки анимации, если есть
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
            .padding(horizontal = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
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
