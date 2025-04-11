package com.lapcevichme.olympiadfinder.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.presentation.viewmodel.OlympiadListViewModel
import java.time.LocalDate

@Composable
fun OlympiadListScreen(
    viewModel: OlympiadListViewModel = hiltViewModel()
) {
    val olympiads by viewModel.olympiads.collectAsState()
    val paginationMetadata by viewModel.paginationMetadata.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Индикатор загрузки (по центру экрана)
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(all = 8.dp)
            ) {
                items(olympiads) { olympiad ->
                    OlympiadItem(olympiad = olympiad)
                }

                // Панель пагинации в конце списка
                item {
                    PaginationPanel(
                        currentPage = currentPage,
                        totalPages = paginationMetadata.totalPages,
                        onPageChange = { viewModel.onPageChanged(it) },
                        onPageSizeChange = { viewModel.onPageSizeChanged(it) },
                        pageSizeOptions = listOf(10, 20, 50)
                    )
                }
            }
        }
    }
}

@Composable
fun PaginationPanel(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    onPageSizeChange: (Int) -> Unit,
    pageSizeOptions: List<Int>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Кнопки "Предыдущая" и "Следующая"
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (currentPage > 1) onPageChange(currentPage - 1) },
                enabled = currentPage > 1
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Page")
            }
            Text("Page $currentPage/$totalPages")
            IconButton(
                onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
                enabled = currentPage < totalPages
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Page")
            }
        }

        // Выпадающий список для выбора размера страницы
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Items per page:")
            var expanded by remember { mutableStateOf(false) }
            var selectedOptionText by remember { mutableStateOf(pageSizeOptions.first()) }

            Box {
                TextButton(onClick = { expanded = true }) {
                    Text(selectedOptionText.toString())
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Page Size Options")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    pageSizeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.toString()) },
                            onClick = {
                                selectedOptionText = option
                                onPageSizeChange(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OlympiadItem(olympiad: Olympiad) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = olympiad.name, style = MaterialTheme.typography.headlineSmall)
            olympiad.subjects?.takeIf { it.isNotEmpty() }?.let { subjects ->
                Text(text = "Предметы: ${subjects.joinToString { it.name }}", style = MaterialTheme.typography.bodyMedium)
            } ?: Text(text = "Предметы: -", style = MaterialTheme.typography.bodyMedium)

            olympiad.minGrade?.let { min ->
                olympiad.maxGrade?.let { max ->
                    Text(text = "Классы: $min - $max", style = MaterialTheme.typography.bodyMedium)
                } ?: Text(text = "Класс: от $min", style = MaterialTheme.typography.bodyMedium)
            } ?: olympiad.maxGrade?.let { max ->
                Text(text = "Класс: до $max", style = MaterialTheme.typography.bodyMedium)
            } ?: Text(text = "Классы: -", style = MaterialTheme.typography.bodyMedium)

            olympiad.stages?.takeIf { it.isNotEmpty() }?.let { stages ->
                Text(text = "Этапы: ${stages.joinToString { it.name }}", style = MaterialTheme.typography.bodySmall)
            } ?: Text(text = "Этапы: -", style = MaterialTheme.typography.bodySmall)

            olympiad.description?.let { description ->
                Text(text = description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            } ?: Text(text = "Описание отсутствует", style = MaterialTheme.typography.bodySmall, maxLines = 2)
        }
    }
}

/*
    ---- PREVIEWS ----
    (made by gemini <3)
 */

@Preview(showBackground = true)
@Composable
fun OlympiadItemPreview() {
    MaterialTheme {
        OlympiadItem(
            olympiad = Olympiad(
                id = 1L,
                name = "Математика Плюс",
                subjects = listOf(Subject("Математика"), Subject("Логика")),
                minGrade = 7,
                maxGrade = 11,
                stages = listOf(
                    Stage("Отборочный", LocalDate.of(2025, 10, 1), LocalDate.of(2025, 11, 15)),
                    Stage("Заключительный", LocalDate.of(2026, 3, 1), null)
                ),
                link = "https://mathplus.ru",
                description = "Олимпиада по математике для школьников 7-11 классов. Включает задания повышенной сложности.",
                keywords = "математика, олимпиада, школьники"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OlympiadItemShortPreview() {
    MaterialTheme {
        OlympiadItem(
            olympiad = Olympiad(
                id = 2L,
                name = "Русский Медвежонок",
                subjects = listOf(Subject("Русский язык")),
                minGrade = 1,
                maxGrade = 11,
                stages = listOf(
                    Stage("Основной тур", LocalDate.of(2025, 11, 15), null)
                ),
                link = "https://rm.ru",
                description = "Международная олимпиада по русскому языку.",
                keywords = "русский язык, олимпиада, медвежонок"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OlympiadItemNoGradesPreview() {
    MaterialTheme {
        OlympiadItem(
            olympiad = Olympiad(
                id = 3L,
                name = "Олимпиада без указания классов",
                subjects = listOf(Subject("Разные")),
                minGrade = null,
                maxGrade = null,
                stages = listOf(
                    Stage("Первый этап", LocalDate.now(), LocalDate.now().plusWeeks(2))
                ),
                link = null,
                description = "Описание олимпиады без указания минимального и максимального классов.",
                keywords = "разное, олимпиада"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OlympiadItemNullableDataPreview() {
    MaterialTheme {
        OlympiadItem(
            olympiad = Olympiad(
                id = 3L,
                name = "Олимпиада с неполными данными",
                subjects = null,
                minGrade = null,
                maxGrade = 9,
                stages = null,
                link = null,
                description = null,
                keywords = null
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OlympiadItemEmptyListsPreview() {
    MaterialTheme {
        OlympiadItem(
            olympiad = Olympiad(
                id = 4L,
                name = "Олимпиада с пустыми списками",
                subjects = emptyList(),
                minGrade = 5,
                maxGrade = null,
                stages = emptyList(),
                link = "http://example.com",
                description = "",
                keywords = ""
            )
        )
    }
}