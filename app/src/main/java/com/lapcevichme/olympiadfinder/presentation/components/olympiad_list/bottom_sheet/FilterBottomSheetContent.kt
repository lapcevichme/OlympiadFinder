package com.lapcevichme.olympiadfinder.presentation.components.olympiad_list.bottom_sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheetContent(
    availableGrades: List<Int>,
    selectedGrades: List<Int>,
    onGradeSelected: (grade: Int, isSelected: Boolean) -> Unit,
    // TODO: Параметры для фильтров по предметам
    onApplyFilters: () -> Unit, // Функция, вызываемая при нажатии кнопки "Применить"
    onResetFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Фильтры", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Раздел "Класс участия"
        Text("Класс участия", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow( // Используем FlowRow для автоматического переноса элементов
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Горизонтальные отступы
            verticalArrangement = Arrangement.spacedBy(4.dp) // Вертикальные отступы
        ) {
            availableGrades.forEach { grade ->
                FilterChip( // Используем FilterChip для выбора
                    selected = grade in selectedGrades, // Чип выбран, если класс есть в selectedGrades
                    onClick = {
                        onGradeSelected(
                            grade,
                            grade !in selectedGrades
                        ) // Переключаем состояние выбора
                    },
                    label = { Text(grade.toString()) }
                )
            }
        }

        // TODO: Раздел "Предметы"

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопки "Сбросить" и "Применить"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onResetFilters, // Вызываем функцию сброса UI состояния
                modifier = Modifier.weight(1f) // Растягиваем кнопку
            ) {
                Text("Сбросить")
            }
            Button(
                onClick = onApplyFilters, // Вызываем функцию применения фильтров (она также закроет лист)
                modifier = Modifier.weight(1f) // Растягиваем кнопку
            ) {
                Text("Применить")
            }
        }
        Spacer(modifier = Modifier.height(16.dp)) // Отступ снизу

    }
}