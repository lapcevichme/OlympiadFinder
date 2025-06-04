package com.lapcevichme.olympiadfinder.presentation.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lapcevichme.olympiadfinder.domain.model.AppError
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginationMetadata
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.presentation.components.ErrorDisplay
import com.lapcevichme.olympiadfinder.presentation.components.olympiad_list.OlympiadItem
import com.lapcevichme.olympiadfinder.presentation.components.olympiad_list.PaginationPanel
import com.lapcevichme.olympiadfinder.presentation.components.olympiad_list.bottom_sheet.FilterBottomSheetContent
import com.lapcevichme.olympiadfinder.presentation.components.olympiad_list.bottom_sheet.OlympiadDetailsSheetContent
import com.lapcevichme.olympiadfinder.presentation.viewmodel.FilterUiState
import com.lapcevichme.olympiadfinder.presentation.viewmodel.OlympiadListViewModel
import com.lapcevichme.olympiadfinder.ui.theme.PreviewTheme
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OlympiadListScreen(
    viewModel: OlympiadListViewModel = hiltViewModel()
) {
    println("OlympiadListScreen: ViewModel hashCode = ${viewModel.hashCode()}")

    val olympiads by viewModel.olympiads.collectAsState()
    val paginationMetadata by viewModel.paginationMetadata.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val displayedPage by viewModel.displayedPage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorState.collectAsState()

    // animations
    val animatePageTransitions by viewModel.animatePageTransitions.collectAsState()
    val animateListItems by viewModel.animateListItems.collectAsState()

    // search
    val searchQuery by viewModel.searchQuery.collectAsState()

    val availableGrades = viewModel.availableGrades

    val availableSubjects by viewModel.availableSubjects.collectAsState()

    val filtersUiState by viewModel.filtersUiState.collectAsState()

    println("OlympiadListScreen: olympiads.pageSize = ${olympiads.size}")
    println("OlympiadListScreen: paginationMetadata = $paginationMetadata")
    println("OlympiadListScreen: currentPage = $currentPage")
    println("OlympiadListScreen: isLoading = $isLoading")

    var selectedOlympiad by remember { mutableStateOf<Olympiad?>(null) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    var showFilterSheet by remember { mutableStateOf(false) }
    val filterSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val listState: LazyListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    // LaunchedEffect для сброса скролла при смене отображаемой страницы
    LaunchedEffect(displayedPage, paginationMetadata.pageSize) {
        println("OlympiadListScreen: Displayed page changed to $displayedPage, scrolling to top.")
        // Проверяем, что список не пуст, прежде чем пытаться скроллить
        if (listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0) {
            listState.scrollToItem(index = 0)
        }
    }

    // Основной контент экрана вынесен в отдельную Composable
    OlympiadListScreenContent(
        olympiads = olympiads,
        currentPage = currentPage,
        totalPages = paginationMetadata.totalPages,
        isLoading = isLoading,
        error = error,
        searchQuery = searchQuery,
        animatePageTransitions = animatePageTransitions,
        animateListItems = animateListItems,
        listState = listState, // Передаем состояние списка
        onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
        onFilterIconClick = { showFilterSheet = true }, // Открываем фильтр лист
        onPageChanged = { viewModel.onPageChanged(it) },
        onOlympiadClick = { olympiad ->
            selectedOlympiad = olympiad
            scope.launch { sheetState.show() }
        },
        onRetryClicked = { viewModel.onRetryClicked() }
    )


    // BottomSheet для деталей олимпиады
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

    // BottomSheet для выбора фильтров
    if (showFilterSheet) {
        ModalBottomSheet(
            sheetState = filterSheetState,
            onDismissRequest = {
                viewModel.discardFilterChanges() // Сбрасывает UI состояние к активным фильтрам
                showFilterSheet = false // Скрывает лист
            },
            content = {
                FilterBottomSheetContent(
                    availableGrades = availableGrades,
                    selectedGrades = filtersUiState.selectedGrades, // Передаем выбранные классы ИЗ СОСТОЯНИЯ UI ЛИСТА
                    onGradeSelected = { grade, isSelected ->
                        viewModel.onGradeFilterUiChanged(
                            grade,
                            isSelected
                        ) // Обновляет СОСТОЯНИЕ UI ЛИСТА
                    },
                    availableSubjects = availableSubjects, // <-- ПЕРЕДАЕМ ДОСТУПНЫЕ ПРЕДМЕТЫ -->
                    selectedSubjects = filtersUiState.selectedSubjects, // <-- ПЕРЕДАЕМ ВЫБРАННЫЕ ПРЕДМЕТЫ ИЗ UI СОСТОЯНИЯ -->
                    onSubjectSelected = { subjectId, isSelected -> // <-- ПЕРЕДАЕМ КОЛЛБЭК ДЛЯ ВЫБОРА ПРЕДМЕТА -->
                        viewModel.onSubjectFilterUiChanged(
                            subjectId,
                            isSelected
                        )
                    },

                    // Колбэк для кнопки "Применить"
                    onApplyFilters = {
                        viewModel.applyFilters() // Вызываем ViewModel для ПРИМЕНЕНИЯ фильтров
                        // Скрываем лист после применения
                        scope.launch {
                            filterSheetState.hide()
                        }.invokeOnCompletion {
                            if (!filterSheetState.isVisible) {
                                showFilterSheet = false
                            }
                        }
                    },

                    // Колбэк для кнопки "Сбросить"
                    onResetFilters = {
                        viewModel.resetAndApplyFilters() // Вызываем ViewModel для СБРОСА И ПРИМЕНЕНИЯ фильтров
                        // Скрываем лист после сброса и применения
                        scope.launch {
                            filterSheetState.hide()
                        }.invokeOnCompletion {
                            if (!filterSheetState.isVisible) {
                                showFilterSheet = false
                            }
                        }
                    }
                )
            }
        )
    }
}


// Stateless Composable для UI контента OlympiadListScreen
@Composable
private fun OlympiadListScreenContent(
    olympiads: List<Olympiad>,
    currentPage: Int,
    totalPages: Int,
    isLoading: Boolean,
    error: AppError?,
    searchQuery: String,
    animatePageTransitions: Boolean,
    animateListItems: Boolean,
    listState: LazyListState, // Принимаем состояние списка
    onSearchQueryChanged: (String) -> Unit,
    onFilterIconClick: () -> Unit,
    onPageChanged: (Int) -> Unit,
    onOlympiadClick: (Olympiad) -> Unit,
    onRetryClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Устанавливаем фон экрана из темы
    ) {
        // Search Bar Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface) // Фон строки поиска
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Поле ввода поискового запроса
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged, // Используем переданный колбэк
                label = {
                    Text(
                        "Поиск олимпиад...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }, // Цвет метки
                singleLine = true,
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Иконка поиска",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }, // Цвет иконки
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) { // Используем переданный колбэк
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Очистить поиск",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            ) // Цвет иконки
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    capitalization = KeyboardCapitalization.Words
                ),
                // Цвета OutlinedTextField обычно берутся из темы автоматически, но можно переопределить
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    // ... другие цвета при необходимости
                ),
                modifier = Modifier.weight(1f) // Занимает все доступное место, кроме кнопки фильтров
            )

            // Кнопка фильтров
            IconButton(onClick = onFilterIconClick) { // Используем переданный колбэк
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Фильтры",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) { // Box занимает оставшееся место под строкой поиска

            // Отображаем контент при УСПЕХЕ (список, пустое состояние, загрузка в пустой список)
            // Этот блок активен ТОЛЬКО если нет активной ошибки
            if (error == null) { // Изменено: проверяем, что error == null
                // Используем AnimatedContent для анимации смены страниц, если данные загружены успешно
                AnimatedContent(
                    targetState = currentPage, // <-- Анимируем по НОМЕРУ ТЕКУЩЕЙ страницы
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {
                        if (animatePageTransitions) {
                            // Определяем анимацию в зависимости от того, вперед или назад листаем
                            if (targetState > initialState) {
                                // Новая страница > Старой (листаем вперед)
                                slideInHorizontally { fullWidth -> fullWidth } + fadeIn() togetherWith
                                        slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut()
                            } else {
                                // Новая страница < Старой (листаем назад)
                                slideInHorizontally { fullWidth -> -fullWidth } + fadeIn() togetherWith
                                        slideOutHorizontally { fullWidth -> fullWidth } + fadeOut()
                            }.using(
                                // Можно добавить SizeTransform, чтобы избежать резких скачков размера, если высота списков разная
                                SizeTransform(clip = false)
                            )
                        } else {
                            EnterTransition.None togetherWith ExitTransition.None
                        }
                    },
                    label = "OlympiadListPageAnimation"
                ) { _ -> // Лямбда получает НОМЕР ТЕКУЩЕЙ страницы
                    Column { // Внутренний Column для содержимого страницы (список, пусто, загрузка)
                        // Проверяем состояние загрузки и список ВНУТРИ AnimatedContent, когда нет ошибки
                        when {
                            // Индикатор загрузки при пустом списке (при первом запуске или сбросе)
                            // Показываем его, только если список пуст Идет загрузка И нет ошибки
                            isLoading && olympiads.isEmpty() -> { // error == null уже проверено выше
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) } // Цвет индикатора
                            }

                            olympiads.isNotEmpty() -> {
                                LazyColumn(
                                    contentPadding = PaddingValues(all = 8.dp),
                                    state = listState, // Используем переданное состояние списка
                                    modifier = Modifier.weight(1f)
                                ) {
                                    items(
                                        items = olympiads,
                                        key = { olympiad -> olympiad.id }
                                    ) { olympiad ->
                                        OlympiadItem(
                                            olympiad = olympiad,
                                            onClick = { onOlympiadClick(olympiad) },
                                            animate = animateListItems,
                                            /*
                                            modifier = if (animateListItems) Modifier.animateItem(
                                                fadeInSpec = tween(durationMillis = 300, delayMillis = 10)
                                            ) else Modifier
                                             */
                                            // При добавлении animateItem периодически ломается LazyColumn
                                        )
                                    }
                                }
                                // Индикатор загрузки внизу списка (когда грузится следующая страница)
                                if (isLoading && olympiads.isNotEmpty()) {
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp),
                                        color = MaterialTheme.colorScheme.primary // Цвет индикатора
                                    )
                                }
                            }
                            // Если список пуст И нет ошибки (и не идет загрузка в пустой список)
                            else -> { // olympiads.isEmpty() && !isLoading // error == null уже проверено выше
                                // Сообщение о пустом списке (если нет ошибки и ничего не найдено)
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Ничего не найдено или олимпиады отсутствуют.", // Упрощенное сообщение для общей Composable
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Отображаем компонент ErrorDisplay ТОЛЬКО если есть ошибка
            // Этот блок активен ТОЛЬКО если error НЕ null
            if (error != null) { // Изменено: проверяем, что error НЕ null
                ErrorDisplay( // Используем универсальный компонент ErrorDisplay
                    error = error, // Передаем текущее состояние ошибки (AppError?)
                    onRetryClicked = onRetryClicked,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Панель пагинации отображается только при отсутствии ошибки и более одной страницы
        if (totalPages > 1 && error == null) {
            PaginationPanel(
                currentPage = currentPage,
                totalPages = totalPages,
                onPageChange = onPageChanged,
                animateTransitions = animatePageTransitions
            )
        }
    }
}


// --- PREVIEWS ДЛЯ OlympiadListScreenContent ---

// Вспомогательные данные для превью
private val sampleOlympiads = List(10) { index ->
    Olympiad(
        id = (index + 1).toLong(),
        name = "Олимпиада ${index + 1}",
        description = "Описание олимпиады ${index + 1}.",
        minGrade = 5 + index % 7,
        maxGrade = 11,
        subjects = listOf(Subject(0, "Предмет ${index % 3 + 1}")),
        stages = listOf(
            Stage(
                "Этап 1",
                LocalDate.of(2024, 12, 31),
                LocalDate.of(2025, 1, 15)
            )
        ),
        link = "http://example.com/${index + 1}",
        keywords = "Ключевое слово ${index + 1}"
    )
}

private val samplePaginationMetadata = PaginationMetadata(
    currentPage = 1,
    totalPages = 5,
    pageSize = 10,
    totalItems = 50
)

// Превью: Состояние загрузки (пустой список)
@Preview(showBackground = true, name = "Loading (Empty) - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Loading (Empty) - Dark Theme"
)
@Composable
fun PreviewOlympiadListScreen_LoadingEmpty() {
    PreviewTheme {
        OlympiadListScreenContent(
            olympiads = emptyList(), // Пустой список
            currentPage = 1,
            totalPages = samplePaginationMetadata.totalPages,
            isLoading = true, // Идет загрузка
            error = null, // Нет ошибки
            searchQuery = "",
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(), // Мок состояния списка
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {}
        )
    }
}

// Превью: Состояние загрузки (загрузка следующей страницы, список не пуст)
@Preview(showBackground = true, name = "Loading (Next Page) - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Loading (Next Page) - Dark Theme"
)
@Composable
fun PreviewOlympiadListScreen_LoadingNextPage() {
    PreviewTheme {
        OlympiadListScreenContent(
            olympiads = sampleOlympiads, // Список не пуст
            currentPage = 1,
            totalPages = samplePaginationMetadata.totalPages,
            isLoading = true, // Идет загрузка
            error = null, // Нет ошибки
            searchQuery = "",
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(), // Мок состояния списка
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {}
        )
    }
}


// Превью: Состояние загружено (с данными, первая страница)
@Preview(showBackground = true, name = "Loaded (Data, Page 1) - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Loaded (Data, Page 1) - Dark Theme"
)
@Composable
fun PreviewOlympiadListScreen_LoadedDataPage1() {
    PreviewTheme {
        OlympiadListScreenContent(
            olympiads = sampleOlympiads, // Список с данными

            currentPage = 1,
            totalPages = samplePaginationMetadata.totalPages,
            isLoading = false, // Загрузка завершена
            error = null, // Нет ошибки
            searchQuery = "",

            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(),
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {},

        )
    }
}

// Превью: Состояние загружено (с данными, средняя страница)
@Preview(showBackground = true, name = "Loaded (Data, Middle Page) - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Loaded (Data, Middle Page) - Dark Theme"
)
@Composable
fun PreviewOlympiadListScreen_LoadedDataMiddlePage() {
    PreviewTheme {
        OlympiadListScreenContent(
            olympiads = sampleOlympiads, // Список с данными

            currentPage = 3,
            totalPages = samplePaginationMetadata.totalPages,
            isLoading = false, // Загрузка завершена
            error = null, // Нет ошибки
            searchQuery = "",

            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(),
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {},

        )
    }
}


// Превью: Состояние загружено (пустой список, нет поиска/фильтров)
@Preview(showBackground = true, name = "Loaded (Empty, No Search/Filters) - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Loaded (Empty, No Search/Filters) - Dark Theme"
)
@Composable
fun PreviewOlympiadListScreen_LoadedEmptyNoSearchFilters() {
    PreviewTheme {
        OlympiadListScreenContent(
            olympiads = emptyList(), // Пустой список
            currentPage = 1,
            totalPages = 0, // Нет страниц
            isLoading = false, // Загрузка завершена
            error = null, // Нет ошибки
            searchQuery = "", // Нет поиска
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(),
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {},
        )
    }
}

// Превью: Состояние загружено (пустой список, с поиском/фильтрами)
@Preview(showBackground = true, name = "Loaded (Empty, With Search/Filters) - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Loaded (Empty, With Search/Filters) - Dark Theme"
)
@Composable
fun PreviewOlympiadListScreen_LoadedEmptyWithSearchFilters() {
    PreviewTheme {
        OlympiadListScreenContent(
            olympiads = emptyList(), // Пустой список
            currentPage = 1,
            totalPages = 0, // Нет страниц
            isLoading = false, // Загрузка завершена
            error = null, // Нет ошибки
            searchQuery = "Математика", // Активный поиск
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(),
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {}
        )
    }
}


// Превью: Состояние сетевой ошибки
@Preview(showBackground = true, name = "Network Error - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Network Error - Dark Theme"
)
@Composable
fun PreviewOlympiadListScreen_NetworkError() {
    PreviewTheme {
        OlympiadListScreenContent(
            olympiads = emptyList(), // Список может быть пустым или нет, но ошибка перекрывает
            currentPage = 1,
            totalPages = samplePaginationMetadata.totalPages,
            isLoading = false, // Загрузка, возможно, завершилась ошибкой
            error = null, // Сетевая ошибка
            searchQuery = "",
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(),
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {},
        )
    }
}

// Превью: Состояние серверной ошибки (с сообщением)
@Preview(showBackground = true, name = "Server Error (With Message) - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Server Error (With Message) - Dark Theme"
)
@Composable
fun PreviewOlympiadListScreen_ServerErrorWithMessage() {
    PreviewTheme {
        OlympiadListScreenContent(
            olympiads = emptyList(), // Список может быть пустым или нет
            currentPage = 1,
            totalPages = samplePaginationMetadata.totalPages,
            isLoading = false,
            error = AppError.ServerError("Ошибка при обработке запроса на сервере."), // Серверная ошибка с сообщением
            searchQuery = "",
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(),
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {},
        )
    }
}

// Превью: Состояние серверной ошибки (без сообщения)
@Preview(showBackground = true, name = "Server Error (No Message) - Light Theme")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Server Error (No Message) - Dark Theme"
)
@Composable
fun PreviewOlympiadListScreen_ServerErrorNoMessage() {
    PreviewTheme {
        OlympiadListScreenContent(
            olympiads = emptyList(), // Список может быть пустым или нет
            currentPage = 1,
            totalPages = samplePaginationMetadata.totalPages,
            isLoading = false,
            error = AppError.ServerError(null), // Серверная ошибка без сообщения
            searchQuery = "",
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(),
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {}
        )
    }
}
