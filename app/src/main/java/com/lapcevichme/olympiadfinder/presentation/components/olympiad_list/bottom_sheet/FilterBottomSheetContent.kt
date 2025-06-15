package com.lapcevichme.olympiadfinder.presentation.components.olympiad_list.bottom_sheet

import android.content.res.Configuration
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lapcevichme.olympiadfinder.domain.model.AppError
import com.lapcevichme.olympiadfinder.domain.model.Resource
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.ui.theme.PreviewTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheetContent(
    availableGrades: List<Int>,
    selectedGrades: List<Int>,
    onGradeSelected: (grade: Int, isSelected: Boolean) -> Unit,
    availableSubjects: Resource<List<Subject>>, // Доступные предметы из ViewModel
    selectedSubjects: List<Long>, // Выбранные предметы (их ID) из UI состояния
    onSubjectSelected: (subjectId: Long, isSelected: Boolean) -> Unit,
    onApplyFilters: () -> Unit,
    onResetFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Фильтры",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Раздел "Класс участия"
        Text(
            "Класс участия",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

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
                    label = {
                        Text(
                            grade.toString(),
                            color = if (grade in selectedGrades) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Раздел "Предметы"

        Text(
            "Предметы",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        when (availableSubjects) {
            is Resource.Loading -> {
                // Показываем индикатор загрузки, пока предметы грузятся
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Text(
                    "Загрузка предметов...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            is Resource.Success -> {
                val subjects = availableSubjects.data
                if (subjects.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        subjects.forEach { subject ->
                            FilterChip(
                                selected = subject.id in selectedSubjects,
                                onClick = {
                                    onSubjectSelected(
                                        subject.id,
                                        subject.id !in selectedSubjects
                                    )
                                },
                                label = {
                                    Text(
                                        subject.name,
                                        color = if (subject.id in selectedSubjects) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }
                    }
                } else {
                    Text(
                        "Предметы не найдены.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is Resource.Failure -> {
                // Показываем сообщение об ошибке, если загрузка предметов не удалась
                val errorMessage = when (availableSubjects.appError) {
                    is AppError.NetworkError -> "Ошибка сети. Проверьте подключение к интернету."
                    is AppError.ServerError -> "Ошибка сервера: ${availableSubjects.appError.message ?: "Что-то пошло не так на сервере."}"
                    is AppError.DataError -> "Ошибка данных: ${availableSubjects.appError.message ?: "Некорректные данные."}"
                    is AppError.NotFoundError -> "Предметы не найдены."
                    is AppError.UnknownError -> "Неизвестная ошибка: ${availableSubjects.appError.message ?: "Попробуйте еще раз."}"
                }
                Text(
                    "Не удалось загрузить предметы: $errorMessage",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопки "Сбросить" и "Применить"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onResetFilters, // Вызываем функцию сброса UI состояния
                modifier = Modifier.weight(1f)
            ) {
                Text("Сбросить", color = MaterialTheme.colorScheme.onSurface)
            }
            Button(
                onClick = onApplyFilters, // Вызываем функцию применения фильтров (она также закроет лист)
                modifier = Modifier.weight(1f)
            ) {
                Text("Применить", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

    }
}

/*
    ---- PREVIEWS ----
 */

@Preview(showBackground = true, name = "Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Theme"
)
@Composable
fun PreviewFilterBottomSheetContent_Default() {
    PreviewTheme {
        FilterBottomSheetContent(
            availableGrades = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            selectedGrades = listOf(5, 7, 9),
            onGradeSelected = { _, _ -> },
            onApplyFilters = {},
            onResetFilters = {},
            availableSubjects = Resource.Success(
                listOf(
                    Subject(id = 1, name = "Математика"),
                    Subject(id = 2, name = "Физика"),
                    Subject(id = 3, name = "Информатика"),
                    Subject(id = 4, name = "Биология"),
                    Subject(id = 5, name = "Химия")
                )
            ),
            selectedSubjects = listOf(2L, 4L), // Выбраны Физика и Биология
            onSubjectSelected = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Theme"
)
@Composable
fun PreviewFilterBottomSheetContent_NoGradesSelected() {
    PreviewTheme {
        FilterBottomSheetContent(
            availableGrades = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            selectedGrades = emptyList(),
            onGradeSelected = { _, _ -> },
            onApplyFilters = {},
            onResetFilters = {},
            availableSubjects = Resource.Success(
                listOf(
                    Subject(id = 1, name = "Математика"),
                    Subject(id = 2, name = "Физика"),
                    Subject(id = 3, name = "Информатика")
                )
            ),
            selectedSubjects = emptyList(), // Ничего не выбрано
            onSubjectSelected = { _, _ -> }
        )
    }
}