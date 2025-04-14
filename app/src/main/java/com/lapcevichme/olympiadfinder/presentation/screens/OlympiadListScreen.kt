package com.lapcevichme.olympiadfinder.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.presentation.viewmodel.OlympiadListViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OlympiadListScreen(
    viewModel: OlympiadListViewModel = hiltViewModel(rememberNavController().getBackStackEntry("main_graph"))
) {
    println("OlympiadListScreen: ViewModel hashCode = ${viewModel.hashCode()}")

    val olympiads by viewModel.olympiads.collectAsState()
    val paginationMetadata by viewModel.paginationMetadata.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    println("OlympiadListScreen: olympiads.size = ${olympiads.size}")
    println("OlympiadListScreen: paginationMetadata = $paginationMetadata")
    println("OlympiadListScreen: currentPage = $currentPage")
    println("OlympiadListScreen: isLoading = $isLoading")

    var selectedOlympiad by remember { mutableStateOf<Olympiad?>(null) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    // Показываем BottomSheet только если олимпиада выбрана
    if (selectedOlympiad != null) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { selectedOlympiad = null },
            content = {
                OlympiadDetailsSheetContent(
                    olympiad = selectedOlympiad!!,
                    onClose = {
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            // После завершения анимации сбрасываем состояние
                            if (!sheetState.isVisible) { // Доп. проверка, что он действительно скрылся
                                selectedOlympiad = null
                            }
                        }
                    }
                )
            }
        )
    }

    val listState: LazyListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    // Основной контент экрана
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
                contentPadding = PaddingValues(all = 8.dp),
                state = listState
            ) {
                items(olympiads) { olympiad ->
                    OlympiadItem(
                        olympiad = olympiad,
                        onClick = {
                            selectedOlympiad = olympiad
                            scope.launch {
                                sheetState.show()
                            }
                        }
                    )
                }

                item {
                    if (paginationMetadata.totalPages > 0) {
                        PaginationPanel(
                            currentPage = currentPage,
                            totalPages = paginationMetadata.totalPages,
                            onPageChange = { viewModel.onPageChanged(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OlympiadDetailsSheetContent(olympiad: Olympiad, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth() // Занимаем всю ширину
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = olympiad.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Description:", style = MaterialTheme.typography.titleMedium)
        Text(text = olympiad.description ?: "No description available")
        Spacer(modifier = Modifier.height(8.dp))

        olympiad.subjects?.takeIf { it.isNotEmpty() }?.let { subjects ->
            Text("Subjects:", style = MaterialTheme.typography.titleMedium)
            subjects.forEach { subject ->
                Text("- ${subject.name}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        olympiad.stages?.takeIf { it.isNotEmpty() }?.let { stages ->
            Text("Stages:", style = MaterialTheme.typography.titleMedium)
            stages.forEach { stage ->
                Text("- ${stage.name}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        val gradeText = when {
            olympiad.minGrade != null && olympiad.maxGrade != null -> "Grades: ${olympiad.minGrade} - ${olympiad.maxGrade}"
            olympiad.minGrade != null -> "Min Grade: ${olympiad.minGrade}"
            olympiad.maxGrade != null -> "Max Grade: ${olympiad.maxGrade}"
            else -> null
        }
        gradeText?.let {
            Text(it, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }


        olympiad.link?.let {
            Text("Link:", style = MaterialTheme.typography.titleMedium)
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium
            ) // Можно сделать кликабельной ссылкой
            Spacer(modifier = Modifier.height(8.dp))
        }
        olympiad.keywords?.let {
            Text("Keywords:", style = MaterialTheme.typography.titleMedium)
            Text(it, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}


@Composable
fun PaginationPanel(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    if (totalPages <= 1) {
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (currentPage > 1) onPageChange(currentPage - 1) },
            enabled = currentPage > 1
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Page")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "$currentPage / $totalPages",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Page")
        }
    }
}


@Composable
fun OlympiadItem(olympiad: Olympiad, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = olympiad.name, style = MaterialTheme.typography.headlineSmall)
            olympiad.subjects?.takeIf { it.isNotEmpty() }?.let { subjects ->
                Text(
                    text = "Предметы: ${subjects.joinToString { it.name }}",
                    style = MaterialTheme.typography.bodyMedium
                )
            } ?: Text(text = "Предметы: -", style = MaterialTheme.typography.bodyMedium)

            olympiad.minGrade?.let { min ->
                olympiad.maxGrade?.let { max ->
                    Text(text = "Классы: $min - $max", style = MaterialTheme.typography.bodyMedium)
                } ?: Text(text = "Класс: от $min", style = MaterialTheme.typography.bodyMedium)
            } ?: olympiad.maxGrade?.let { max ->
                Text(text = "Класс: до $max", style = MaterialTheme.typography.bodyMedium)
            } ?: Text(text = "Классы: -", style = MaterialTheme.typography.bodyMedium)

            olympiad.stages?.takeIf { it.isNotEmpty() }?.let { stages ->
                Text(
                    text = "Этапы: ${stages.joinToString { it.name }}",
                    style = MaterialTheme.typography.bodySmall
                )
            } ?: Text(text = "Этапы: -", style = MaterialTheme.typography.bodySmall)

            olympiad.description?.let { description ->
                Text(text = description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            } ?: Text(
                text = "Описание отсутствует",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
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
            ),
            onClick = {}
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
            ),
            onClick = {}
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
            ),
            onClick = {}
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
            ),
            onClick = {}
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
            ),
            onClick = {}
        )
    }
}