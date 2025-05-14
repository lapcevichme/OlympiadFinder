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
import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginationMetadata
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.presentation.components.ErrorDisplay
import com.lapcevichme.olympiadfinder.presentation.components.olympiad_list.OlympiadItem
import com.lapcevichme.olympiadfinder.presentation.components.olympiad_list.PaginationPanel
import com.lapcevichme.olympiadfinder.presentation.components.olympiad_list.bottom_sheet.FilterBottomSheetContent
import com.lapcevichme.olympiadfinder.presentation.components.olympiad_list.bottom_sheet.OlympiadDetailsSheetContent
import com.lapcevichme.olympiadfinder.presentation.viewmodel.ErrorState
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
    val errorState by viewModel.errorState.collectAsState()

    // animations
    val animatePageTransitions by viewModel.animatePageTransitions.collectAsState()
    val animateListItems by viewModel.animateListItems.collectAsState()

    // search
    val searchQuery by viewModel.searchQuery.collectAsState()

    val availableGrades = viewModel.availableGrades

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
        paginationMetadata = paginationMetadata,
        currentPage = currentPage,
        totalPages = paginationMetadata.totalPages,
        isLoading = isLoading,
        errorState = errorState,
        searchQuery = searchQuery,
        filterUiState = filtersUiState,
        availableGrades = availableGrades,
        animatePageTransitions = animatePageTransitions,
        animateListItems = animateListItems,
        listState = listState,
        onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
        onFilterIconClick = { showFilterSheet = true },
        onPageChanged = { viewModel.onPageChanged(it) },
        onOlympiadClick = { olympiad ->
            selectedOlympiad = olympiad
            scope.launch { sheetState.show() }
        },
        onRetryClicked = { viewModel.onRetryClicked() },
        onGradeFilterUiChanged = { grade, isSelected ->
            viewModel.onGradeFilterUiChanged(
                grade,
                isSelected
            )
        },
        applyFilters = { viewModel.applyFilters() },
        discardFilterChanges = { viewModel.discardFilterChanges() },
        resetAndApplyFilters = { viewModel.resetAndApplyFilters() }
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
                    // TODO: Добавить параметры для фильтров по предметам

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
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OlympiadListScreenContent(
    olympiads: List<Olympiad>,
    paginationMetadata: PaginationMetadata,
    currentPage: Int,
    totalPages: Int,
    isLoading: Boolean,
    errorState: ErrorState,
    searchQuery: String,
    filterUiState: FilterUiState, // Передаем UI состояние фильтров для отображения
    availableGrades: List<Int>, // Передаем доступные классы для фильтров
    animatePageTransitions: Boolean,
    animateListItems: Boolean,
    listState: LazyListState, // Принимаем состояние списка
    onSearchQueryChanged: (String) -> Unit,
    onFilterIconClick: () -> Unit,
    onPageChanged: (Int) -> Unit,
    onOlympiadClick: (Olympiad) -> Unit,
    onRetryClicked: () -> Unit,
    // Колбэки для фильтров (передаем их из ViewModel через основной Composable)
    onGradeFilterUiChanged: (grade: Int, isSelected: Boolean) -> Unit,
    applyFilters: () -> Unit,
    discardFilterChanges: () -> Unit,
    resetAndApplyFilters: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Search Bar Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Иконка поиска"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) { // Используем переданный колбэк
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Очистить поиск"
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    capitalization = KeyboardCapitalization.Words
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                modifier = Modifier.weight(1f)
            )

            // Кнопка фильтров
            IconButton(onClick = onFilterIconClick) { // Используем переданный колбэк
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Фильтры"
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) { // Box занимает оставшееся место под строкой поиска
            // Отображаем контент при УСПЕХЕ (список, пустое состояние, загрузка в пустой список)
            // Этот блок активен ТОЛЬКО если нет активной ошибки
            if (errorState is ErrorState.NoError) {
                // Используем AnimatedContent для анимации смены страниц, если данные загружены успешно
                AnimatedContent(
                    targetState = currentPage, // <-- Анимируем по НОМЕРУ ТЕКУЩЕЙ страницы
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {
                        if (animatePageTransitions) {
                            if (targetState > initialState) {
                                slideInHorizontally { fullWidth -> fullWidth } + fadeIn() togetherWith slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut()
                            } else {
                                slideInHorizontally { fullWidth -> -fullWidth } + fadeIn() togetherWith slideOutHorizontally { fullWidth -> fullWidth } + fadeOut()
                            }.using(SizeTransform(clip = false))
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
                            isLoading && olympiads.isEmpty() -> { // errorState is ErrorState.NoError уже проверено выше
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
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
                                            onClick = { onOlympiadClick(olympiad) }, // Используем переданный колбэк
                                            // onFavouriteClick = { id, isFav -> viewModel.onFavouriteToggle(id, isFav) }, // - добавлю когда сделаю сохранение избранных
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
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            // Если список пуст И нет ошибки (и не идет загрузка в пустой список)
                            else -> { // olympiads.isEmpty() && !isLoading // errorState is ErrorState.NoError уже проверено выше
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
            // Этот блок активен ТОЛЬКО если errorState НЕ ErrorState.NoError
            if (errorState !is ErrorState.NoError) {
                ErrorDisplay( // Используем универсальный компонент ErrorDisplay
                    errorState = errorState, // Передаем текущее состояние ошибки
                    onRetryClicked = onRetryClicked, // Используем переданный колбэк
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Панель пагинации отображается только при отсутствии ошибки и более одной страницы
        if (totalPages > 1 && errorState is ErrorState.NoError) {
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
        subjects = listOf(Subject("Предмет ${index % 3 + 1}")),
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

private val sampleFilterUiState = FilterUiState(
    selectedGrades = listOf(9, 10, 11)
    // ... другие состояния фильтров
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
            paginationMetadata = samplePaginationMetadata.copy(currentPage = 1),
            currentPage = 1,
            totalPages = samplePaginationMetadata.totalPages,
            isLoading = true, // Идет загрузка
            errorState = ErrorState.NoError, // Нет ошибки
            searchQuery = "",
            filterUiState = FilterUiState(), // Без активных фильтров
            availableGrades = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(), // Мок состояния списка
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {},
            onGradeFilterUiChanged = { _, _ -> },
            applyFilters = {},
            discardFilterChanges = {},
            resetAndApplyFilters = {}
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
            paginationMetadata = samplePaginationMetadata.copy(currentPage = 1),
            currentPage = 1,
            totalPages = samplePaginationMetadata.totalPages,
            isLoading = true, // Идет загрузка
            errorState = ErrorState.NoError, // Нет ошибки
            searchQuery = "",
            filterUiState = FilterUiState(),
            availableGrades = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(), // Мок состояния списка
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {},
            onGradeFilterUiChanged = { _, _ -> },
            applyFilters = {},
            discardFilterChanges = {},
            resetAndApplyFilters = {}
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
            paginationMetadata = samplePaginationMetadata.copy(currentPage = 1),
            currentPage = 1,
            totalPages = samplePaginationMetadata.totalPages,
            isLoading = false, // Загрузка завершена
            errorState = ErrorState.NoError, // Нет ошибки
            searchQuery = "",
            filterUiState = FilterUiState(),
            availableGrades = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(),
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {},
            onGradeFilterUiChanged = { _, _ -> },
            applyFilters = {},
            discardFilterChanges = {},
            resetAndApplyFilters = {}
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
            paginationMetadata = samplePaginationMetadata.copy(currentPage = 3),
            currentPage = 3,
            totalPages = samplePaginationMetadata.totalPages,
            isLoading = false, // Загрузка завершена
            errorState = ErrorState.NoError, // Нет ошибки
            searchQuery = "",
            filterUiState = FilterUiState(),
            availableGrades = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(),
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {},
            onGradeFilterUiChanged = { _, _ -> },
            applyFilters = {},
            discardFilterChanges = {},
            resetAndApplyFilters = {}
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
            paginationMetadata = samplePaginationMetadata.copy(
                currentPage = 1,
                totalItems = 0,
                totalPages = 0
            ), // Нет страниц
            currentPage = 1,
            totalPages = 0, // Нет страниц
            isLoading = false, // Загрузка завершена
            errorState = ErrorState.NoError, // Нет ошибки
            searchQuery = "", // Нет поиска
            filterUiState = FilterUiState(), // Нет фильтров
            availableGrades = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(),
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {},
            onGradeFilterUiChanged = { _, _ -> },
            applyFilters = {},
            discardFilterChanges = {},
            resetAndApplyFilters = {}
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
            paginationMetadata = samplePaginationMetadata.copy(
                currentPage = 1,
                totalItems = 0,
                totalPages = 0
            ), // Нет страниц
            currentPage = 1,
            totalPages = 0, // Нет страниц
            isLoading = false, // Загрузка завершена
            errorState = ErrorState.NoError, // Нет ошибки
            searchQuery = "Математика", // Активный поиск
            filterUiState = sampleFilterUiState, // Активные фильтры
            availableGrades = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(),
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {},
            onGradeFilterUiChanged = { _, _ -> },
            applyFilters = {},
            discardFilterChanges = {},
            resetAndApplyFilters = {}
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
            paginationMetadata = samplePaginationMetadata.copy(currentPage = 1),
            currentPage = 1,
            totalPages = samplePaginationMetadata.totalPages,
            isLoading = false, // Загрузка, возможно, завершилась ошибкой
            errorState = ErrorState.NetworkError, // Сетевая ошибка
            searchQuery = "",
            filterUiState = FilterUiState(),
            availableGrades = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(),
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {},
            onGradeFilterUiChanged = { _, _ -> },
            applyFilters = {},
            discardFilterChanges = {},
            resetAndApplyFilters = {}
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
            paginationMetadata = samplePaginationMetadata.copy(currentPage = 1),
            currentPage = 1,
            totalPages = samplePaginationMetadata.totalPages,
            isLoading = false,
            errorState = ErrorState.ServerError("Ошибка при обработке запроса на сервере."), // Серверная ошибка с сообщением
            searchQuery = "",
            filterUiState = FilterUiState(),
            availableGrades = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(),
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {},
            onGradeFilterUiChanged = { _, _ -> },
            applyFilters = {},
            discardFilterChanges = {},
            resetAndApplyFilters = {}
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
            paginationMetadata = samplePaginationMetadata.copy(currentPage = 1),
            currentPage = 1,
            totalPages = samplePaginationMetadata.totalPages,
            isLoading = false,
            errorState = ErrorState.ServerError(null), // Серверная ошибка без сообщения
            searchQuery = "",
            filterUiState = FilterUiState(),
            availableGrades = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            animatePageTransitions = true,
            animateListItems = true,
            listState = rememberLazyListState(),
            onSearchQueryChanged = {},
            onFilterIconClick = {},
            onPageChanged = {},
            onOlympiadClick = {},
            onRetryClicked = {},
            onGradeFilterUiChanged = { _, _ -> },
            applyFilters = {},
            discardFilterChanges = {},
            resetAndApplyFilters = {}
        )
    }
}
