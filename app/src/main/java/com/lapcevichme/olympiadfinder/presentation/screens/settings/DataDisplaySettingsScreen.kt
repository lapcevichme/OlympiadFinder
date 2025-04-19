package com.lapcevichme.olympiadfinder.presentation.screens.settings

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lapcevichme.olympiadfinder.presentation.viewmodel.SettingsViewModel


@Composable
fun DataDisplaySettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentPageSize by viewModel.currentPageSize.collectAsState()

    val pageSizeOptions = listOf(10, 20, 50)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Отображение данных", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

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
                        onClick = null
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

        // TODO: Добавить другие настройки отображения данных (фильтры по умолчанию и т.д.)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Настройки фильтров (TODO)", style = MaterialTheme.typography.titleMedium)
        // ... UI для фильтров ...
    }
}