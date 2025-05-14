package com.lapcevichme.olympiadfinder.presentation.screens.settings

import android.content.res.Configuration
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lapcevichme.olympiadfinder.presentation.viewmodel.SettingsViewModel
import com.lapcevichme.olympiadfinder.ui.theme.PreviewTheme


@Composable
fun AnimationSettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val animatePageTransitions by viewModel.animatePageTransitions.collectAsState()
    val animateListItems by viewModel.animateListItems.collectAsState()
    val animateThemeChanges by viewModel.animateThemeChanges.collectAsState()

    // UI-логика вынесена в stateless Composable для превью
    AnimationSettingsScreenContent(
        animatePageTransitions = animatePageTransitions,
        animateListItems = animateListItems,
        animateThemeChanges = animateThemeChanges,
        onAnimatePageTransitionsChange = { viewModel.setAnimatePageTransitions(it) },
        onAnimateListItemsChange = { viewModel.setAnimateListItems(it) },
        onAnimateThemeChangesChange = { viewModel.setAnimateThemeChanges(it) }
    )
}

// Stateless Composable для UI контента AnimationSettingsScreen
@Composable
private fun AnimationSettingsScreenContent(
    animatePageTransitions: Boolean,
    animateListItems: Boolean,
    animateThemeChanges: Boolean,
    onAnimatePageTransitionsChange: (Boolean) -> Unit,
    onAnimateListItemsChange: (Boolean) -> Unit,
    onAnimateThemeChangesChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Настройки анимации",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Animation Toggles ---
        AnimationToggle(
            text = "Анимация смены страниц",
            checked = animatePageTransitions,
            onCheckedChange = onAnimatePageTransitionsChange
        )
        AnimationToggle(
            text = "Анимация элементов списка",
            checked = animateListItems,
            onCheckedChange = onAnimateListItemsChange
        )
        AnimationToggle(
            text = "Анимация смены темы",
            checked = animateThemeChanges,
            onCheckedChange = onAnimateThemeChangesChange
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
                .padding(end = 16.dp),
            color = MaterialTheme.colorScheme.onBackground // Цвет текста
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

// --- PREVIEWS ДЛЯ AnimationSettingsScreenContent ---
// Превью: Все анимации включены
@Preview(showBackground = true, name = "All Animations On - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "All Animations On - Dark Theme"
)
@Composable
fun PreviewAnimationSettingsScreen_AllOn() {
    PreviewTheme {
        AnimationSettingsScreenContent(
            animatePageTransitions = true,
            animateListItems = true,
            animateThemeChanges = true,
            onAnimatePageTransitionsChange = {},
            onAnimateListItemsChange = {},
            onAnimateThemeChangesChange = {}
        )
    }
}

// Превью: Все анимации выключены
@Preview(showBackground = true, name = "All Animations Off - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "All Animations Off - Dark Theme"
)
@Composable
fun PreviewAnimationSettingsScreen_AllOff() {
    PreviewTheme {
        AnimationSettingsScreenContent(
            animatePageTransitions = false,
            animateListItems = false,
            animateThemeChanges = false,
            onAnimatePageTransitionsChange = {},
            onAnimateListItemsChange = {},
            onAnimateThemeChangesChange = {}
        )
    }
}

// --- PREVIEWS ДЛЯ AnimationToggle ---
@Preview(showBackground = true, name = "Animation Toggle - Checked - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Animation Toggle - Checked - Dark Theme"
)
@Composable
fun PreviewAnimationToggle_Checked() {
    PreviewTheme {
        AnimationToggle(text = "Toggle Option", checked = true, onCheckedChange = {})
    }
}

@Preview(showBackground = true, name = "Animation Toggle - Unchecked - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Animation Toggle - Unchecked - Dark Theme"
)
@Composable
fun PreviewAnimationToggle_Unchecked() {
    PreviewTheme {
        AnimationToggle(text = "Toggle Option", checked = false, onCheckedChange = {})
    }
}

