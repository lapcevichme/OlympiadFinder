package com.lapcevichme.olympiadfinder.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.presentation.viewmodel.OlympiadListViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OlympiadListScreen(
    viewModel: OlympiadListViewModel = hiltViewModel()
) {
    println("OlympiadListScreen: ViewModel hashCode = ${viewModel.hashCode()}")

    val olympiads by viewModel.olympiads.collectAsState()
    val paginationMetadata by viewModel.paginationMetadata.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // animations
    val animatePageTransitions by viewModel.animatePageTransitions.collectAsState()
    val animateListItems by viewModel.animateListItems.collectAsState()

    // search
    val searchQuery by viewModel.searchQuery.collectAsState()

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

    LaunchedEffect(currentPage, paginationMetadata.pageSize) {
        println("OlympiadListScreen: Page or PageSize changed, scrolling to top.")
        // Запускаем прокрутку к первому элементу при смене страницы.
        // Это делается *после* того, как данные для новой страницы начали загружаться/появились,
        // но до того, как пользователь может активно скроллить и вызвать конфликт состояний.
        // scrollToItem(0) безопаснее во время переходов AnimatedContent.
        listState.scrollToItem(index = 0)
    }


    // Основной контент экрана
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = searchQuery, // Связываем значение поля с StateFlow в ViewModel
            onValueChange = { viewModel.onSearchQueryChanged(it) }, // При вводе текста вызываем функцию ViewModel
            label = { Text("Поиск олимпиад...") }, // Лейбл поля
            singleLine = true, // В одну строку
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Иконка поиска"
                )
            }, // Иконка поиска слева
            trailingIcon = { // Иконка "очистить" справа, если есть текст
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) { // При клике очищаем запрос через ViewModel
                        Icon(Icons.Default.Close, contentDescription = "Очистить поиск")
                    }
                }
                // TODO: Здесь можно добавить иконку фильтров
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search, // Кнопка "Поиск" на клавиатуре
                capitalization = KeyboardCapitalization.Words // Или другое
            ),
            // Опционально: Если нужно реагировать именно на нажатие "Поиск" на клавиатуре, а не на debounce
            // keyboardActions = KeyboardActions(onSearch = { /* Выполнить действие, если нужно */ }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
        )// Растягиваем на всю ширину


        if (isLoading && olympiads.isEmpty()) { // Показываем индикатор только если данных нет совсем
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            AnimatedContent(
                targetState = currentPage,
                modifier = Modifier.weight(1f),
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
                label = "OlympiadListPageAnimation" // Метка для отладки
            ) { _ ->
                Column {
                    if (olympiads.isNotEmpty()) {
                        LazyColumn(
                            contentPadding = PaddingValues(all = 8.dp),
                            state = listState,
                            modifier = Modifier.weight(1f)
                        ) {
                            items(
                                items = olympiads,
                                key = { olympiad -> olympiad.id }
                            ) { olympiad ->

                                val itemModifier = if (animateListItems) {
                                    Modifier.animateItemPlacement()
                                } else {
                                    Modifier
                                }

                                if (animateListItems) {
                                    // Используем AnimatedVisibility для анимации появления (FadeIn)
                                    // Modifier.animateItemPlacement() ЗДЕСЬ БОЛЬШЕ НЕ ПРИМЕНЯЕМ
                                    AnimatedVisibility(
                                        visible = true, // Всегда true, т.к. мы управляем видимостью через if
                                        enter = fadeIn(
                                            animationSpec = tween(
                                                durationMillis = 300,
                                                delayMillis = 100 // Небольшая задержка для эффекта "наплыва"
                                            )
                                        ),
                                        // exit можно не указывать
                                        modifier = Modifier
                                    ) {
                                        OlympiadItem(
                                            olympiad = olympiad,
                                            onClick = {
                                                selectedOlympiad = olympiad
                                                scope.launch {
                                                    sheetState.show()
                                                }
                                            },
                                            animate = true,
                                            modifier = Modifier
                                        )
                                    }
                                } else {
                                    // Анимация выключена - просто показываем элемент
                                    OlympiadItem(
                                        olympiad = olympiad,
                                        onClick = {
                                            selectedOlympiad = olympiad
                                            scope.launch { sheetState.show() }
                                        },
                                        animate = false, // Передаем false, чтобы отключить внутреннюю анимацию нажатия
                                        modifier = itemModifier
                                    )
                                }
                            }
                        }

                        // Индикатор загрузки внизу списка, когда грузится следующая страница
                        if (isLoading && olympiads.isNotEmpty()) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )
                        }
                    } else {
                        // Показываем сообщение, если список пуст после загрузки
                        Box(
                            modifier = Modifier.fillMaxSize().weight(1f), // Занимает оставшееся место
                            contentAlignment = Alignment.Center
                        ) {
                            // Сообщение зависит от того, был ли поисковый запрос или активны ли фильтры
                            // TODO: Добавить проверку активных фильтров
                            if (searchQuery.isNotEmpty() /* || активны фильтры */) {
                                Text("Ничего не найдено по запросу \"$searchQuery\".")
                            } else {
                                Text("Олимпиады не найдены.") // Сообщение, если список пуст без поиска/фильтров
                            }
                        }
                    }
                }
            }


            if (paginationMetadata.totalPages > 0 && !isLoading) {
                PaginationPanel(
                    currentPage = currentPage,
                    totalPages = paginationMetadata.totalPages,
                    onPageChange = { viewModel.onPageChanged(it) },
                    animateTransitions = animatePageTransitions
                )
            }

            if (isLoading && olympiads.isNotEmpty()) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }

        }
    }
}

@Composable
fun OlympiadDetailsSheetContent(olympiad: Olympiad, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
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
    onPageChange: (Int) -> Unit,
    animateTransitions: Boolean
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
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Предыдущая страница")
        }
        Spacer(modifier = Modifier.width(16.dp))
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                if (animateTransitions) {
                    if (targetState > initialState) {
                        slideInVertically { height -> height } + fadeIn() togetherWith
                                slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        slideInVertically { height -> -height } + fadeIn() togetherWith
                                slideOutVertically { height -> height } + fadeOut()
                    }.using(SizeTransform(clip = false))
                } else {
                    EnterTransition.None togetherWith ExitTransition.None // <-- Отключение анимации
                }
            },
            label = "PageNumberAnimation"
        ) { targetPage ->
            Text(
                text = "$targetPage / $totalPages", // Используем targetPage
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Следующая страница")
        }
    }
}


@Composable
fun OlympiadItem(
    olympiad: Olympiad,
    onClick: () -> Unit,
    animate: Boolean,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Анимируем scale только если общий флаг animate включен И элемент нажат
    val scale by animateFloatAsState(
        targetValue = if (isPressed && animate) 0.98f else 1.0f,
        label = "OlympiadItemScale",

        animationSpec = if (animate) tween(durationMillis = 100) else snap()
    )

    Card(
        onClick = onClick,
        // Применяем ПЕРЕДАННЫЙ модификатор первым, затем добавляем внутренние
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then( // Добавляем условный graphicsLayer, если animate включен
                if (animate) {
                    Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                } else {
                    Modifier // Если animate выключен, не применяем graphicsLayer
                }
            ),
        interactionSource = interactionSource,
        shape = MaterialTheme.shapes.medium
        // elevation = CardDefaults.cardElevation(...) // Можно добавить elevation
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

            val gradeRange = when {
                olympiad.minGrade != null && olympiad.maxGrade != null -> "Классы: ${olympiad.minGrade} - ${olympiad.maxGrade}"
                olympiad.minGrade != null -> "Класс: от ${olympiad.minGrade}"
                olympiad.maxGrade != null -> "Класс: до ${olympiad.maxGrade}"
                else -> "Классы: -"
            }
            Text(text = gradeRange, style = MaterialTheme.typography.bodyMedium)

            olympiad.stages?.takeIf { it.isNotEmpty() }?.let { stages ->
                Text(
                    text = "Этапы: ${stages.joinToString { it.name }}",
                    style = MaterialTheme.typography.bodySmall
                )
            } ?: Text(text = "Этапы: -", style = MaterialTheme.typography.bodySmall)

            olympiad.description?.takeIf { it.isNotBlank() }?.let { description ->
                Text(text = description, style = MaterialTheme.typography.bodySmall)
            } ?: Text(text = "Описание отсутствует", style = MaterialTheme.typography.bodySmall)
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
            onClick = {},
            animate = true
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
            onClick = {},
            animate = true
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
            onClick = {},
            animate = true
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
            onClick = {},
            animate = true
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
            onClick = {},
            animate = true
        )
    }
}