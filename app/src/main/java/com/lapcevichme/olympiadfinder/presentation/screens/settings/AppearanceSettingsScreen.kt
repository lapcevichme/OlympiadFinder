package com.lapcevichme.olympiadfinder.presentation.screens.settings

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lapcevichme.olympiadfinder.domain.model.AppFont
import com.lapcevichme.olympiadfinder.domain.model.Theme
import com.lapcevichme.olympiadfinder.presentation.viewmodel.SettingsViewModel
import com.lapcevichme.olympiadfinder.ui.theme.PreviewTheme

@Composable
fun AppearanceSettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val currentFont by viewModel.appFont.collectAsState()

    val fontOptions = AppFont.entries.toList()

    // UI-логика вынесена в stateless Composable для превью
    AppearanceSettingsScreenContent(
        currentTheme = currentTheme,
        currentFont = currentFont,
        fontOptions = fontOptions,
        onThemeSelected = { viewModel.changeTheme(it) },
        onFontSelected = { viewModel.changeFont(it) }
    )
}

// Stateless Composable для UI контента AppearanceSettingsScreen
@Composable
private fun AppearanceSettingsScreenContent(
    currentTheme: Theme,
    currentFont: AppFont,
    fontOptions: List<AppFont>,
    onThemeSelected: (Theme) -> Unit,
    onFontSelected: (AppFont) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            "Внешний вид",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Theme Selection ---
        Text(
            "Тема приложения",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(Modifier.selectableGroup()) {
            ThemeRadioButton(
                text = "Светлая",
                selected = currentTheme == Theme.LIGHT,
                onClick = { onThemeSelected(Theme.LIGHT) } // Используем переданный колбэк
            )
            ThemeRadioButton(
                text = "Темная",
                selected = currentTheme == Theme.DARK,
                onClick = { onThemeSelected(Theme.DARK) } // Используем переданный колбэк
            )
            ThemeRadioButton(
                text = "Системная",
                selected = currentTheme == Theme.SYSTEM,
                onClick = { onThemeSelected(Theme.SYSTEM) } // Используем переданный колбэк
            )
        }

        // TODO: Добавить другие настройки внешнего вида (шрифты и т.д.)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Шрифт приложения",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(Modifier.selectableGroup()) {
            fontOptions.forEach { fontOption ->
                ThemeRadioButton(
                    text = when (fontOption) {
                        AppFont.DEFAULT -> "Стандартный"
                        AppFont.SERIF -> "С засечками (Serif)"
                        AppFont.MONOSPACE -> "Моноширинный (Monospace)"
                    },
                    selected = currentFont == fontOption,
                    onClick = { onFontSelected(fontOption) } // Используем переданный колбэк
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Настройки шрифта (TODO)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
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
            onClick = null // Клик обрабатывается родительским selectable
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// --- PREVIEWS ДЛЯ AppearanceSettingsScreenContent ---
// Превью: Стандартный шрифт
@Preview(showBackground = true, name = "Light Theme, Default Font")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Theme, Default Font"
)
@Composable
fun PreviewAppearanceSettingsScreen_Light_Default() {
    PreviewTheme {
        AppearanceSettingsScreenContent(
            currentTheme = Theme.SYSTEM,
            currentFont = AppFont.DEFAULT,
            fontOptions = AppFont.entries.toList(),
            onThemeSelected = {},
            onFontSelected = {}
        )
    }
}

// Превью: Шрифт с засечками
@Preview(showBackground = true, name = "Light Theme, Serif Font")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Theme, Serif Font"
)
@Composable
fun PreviewAppearanceSettingsScreen_SystemLight_Serif() {
    PreviewTheme {
        AppearanceSettingsScreenContent(
            currentTheme = Theme.SYSTEM,
            currentFont = AppFont.SERIF,
            fontOptions = AppFont.entries.toList(),
            onThemeSelected = {},
            onFontSelected = {}
        )
    }
}

// Превью: Моноширинный шрифт
@Preview(showBackground = true, name = "Light Theme, Monospace Font")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Theme, Monospace Font"
)
@Composable
fun PreviewAppearanceSettingsScreen_SystemDark_Monospace() {
    PreviewTheme {
        AppearanceSettingsScreenContent(
            currentTheme = Theme.SYSTEM,
            currentFont = AppFont.MONOSPACE,
            fontOptions = AppFont.entries.toList(),
            onThemeSelected = {},
            onFontSelected = {}
        )
    }
}

// --- PREVIEWS ДЛЯ ThemeRadioButton ---
@Preview(showBackground = true, name = "Theme Radio Button - Selected - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Theme Radio Button - Selected - Dark Theme"
)
@Composable
fun PreviewThemeRadioButton_Selected() {
    PreviewTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        ) {
            ThemeRadioButton(text = "Selected Option", selected = true, onClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Theme Radio Button - Unselected - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Theme Radio Button - Unselected - Dark Theme"
)
@Composable
fun PreviewThemeRadioButton_Unselected() {
    PreviewTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        ) {
            ThemeRadioButton(text = "Unselected Option", selected = false, onClick = {})
        }
    }
}