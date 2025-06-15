package com.lapcevichme.olympiadfinder.presentation.screens.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.lapcevichme.olympiadfinder.presentation.viewmodel.SettingsViewModel
import com.lapcevichme.olympiadfinder.ui.theme.PreviewTheme


@Composable
fun DataDisplaySettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentPageSize by viewModel.currentPageSize.collectAsState()

    val pageSizeOptions = listOf(10, 20, 50)

    DataDisplaySettingsScreenContent(
        currentPageSize = currentPageSize,
        pageSizeOptions = pageSizeOptions,
        onPageSizeSelected = { viewModel.changePageSize(it) }
    )
}

// Stateless Composable для UI контента DataDisplaySettingsScreen
@Composable
private fun DataDisplaySettingsScreenContent(
    currentPageSize: Int,
    pageSizeOptions: List<Int>,
    onPageSizeSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Отображение данных",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Page Size Selection ---
        Text(
            "Элементов на странице",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.selectableGroup()) {
            pageSizeOptions.forEach { sizeOption ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(48.dp)
                        .selectable(
                            selected = (currentPageSize == sizeOption),
                            onClick = { onPageSizeSelected(sizeOption) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 8.dp)
                ) {
                    RadioButton(
                        selected = (currentPageSize == sizeOption),
                        onClick = null // Клик обрабатывается родительским selectable
                    )
                    Text(
                        text = sizeOption.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(Modifier.width(12.dp))
            }
        }

        // TODO: Добавить другие настройки отображения данных (фильтры по умолчанию и т.д.)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Настройки фильтров (TODO)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        // ... UI для фильтров ...
    }
}

// --- PREVIEWS ДЛЯ DataDisplaySettingsScreenContent ---
// Превью с выбранной опцией 10
@Preview(showBackground = true, name = "Page Size 10 - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Page Size 10 - Dark Theme"
)
@Composable
fun PreviewDataDisplaySettingsScreen_10() {
    PreviewTheme {
        DataDisplaySettingsScreenContent(
            currentPageSize = 10,
            pageSizeOptions = listOf(10, 20, 50),
            onPageSizeSelected = {}
        )
    }
}

// Превью с выбранной опцией 20
@Preview(showBackground = true, name = "Page Size 20 - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Page Size 20 - Dark Theme"
)
@Composable
fun PreviewDataDisplaySettingsScreen_20() {
    PreviewTheme {
        DataDisplaySettingsScreenContent(
            currentPageSize = 20,
            pageSizeOptions = listOf(10, 20, 50),
            onPageSizeSelected = {}
        )
    }
}

// Превью с выбранной опцией 50
@Preview(showBackground = true, name = "Page Size 50 - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Page Size 50 - Dark Theme"
)
@Composable
fun PreviewDataDisplaySettingsScreen_50() {
    PreviewTheme {
        DataDisplaySettingsScreenContent(
            currentPageSize = 50,
            pageSizeOptions = listOf(10, 20, 50),
            onPageSizeSelected = {}
        )
    }
}