package com.lapcevichme.olympiadfinder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lapcevichme.olympiadfinder.data.local.DEFAULT_PAGE_SIZE
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginationMetadata
import com.lapcevichme.olympiadfinder.domain.usecases.GetPaginatedOlympiadsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.GetPageSizePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimateListItemsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimatePageTransitionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

val AVAILABLE_GRADES = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)

@HiltViewModel
class OlympiadListViewModel @Inject constructor(
    private val getPaginatedOlympiadsUseCase: GetPaginatedOlympiadsUseCase,
    getPageSizePreferenceUseCase: GetPageSizePreferenceUseCase,
    getAnimatePageTransitionsUseCase: GetAnimatePageTransitionsUseCase,
    getAnimateListItemsUseCase: GetAnimateListItemsUseCase
) : ViewModel() {

    private val _olympiads = MutableStateFlow<List<Olympiad>>(emptyList())
    val olympiads: StateFlow<List<Olympiad>> = _olympiads

    // Используем StateFlow для pageSize, полученный из настроек
    private val pageSize: StateFlow<Int> = getPageSizePreferenceUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = DEFAULT_PAGE_SIZE
        )

    // StateFlow для animatePageTransitions
    val animatePageTransitions: StateFlow<Boolean> = getAnimatePageTransitionsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    // StateFlow для animateListItems
    val animateListItems: StateFlow<Boolean> = getAnimateListItemsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    private val _paginationMetadata = MutableStateFlow(
        PaginationMetadata(
            totalItems = 0,
            totalPages = 1,
            currentPage = 1,
            pageSize = pageSize.value
        ) // Используем начальное значение pageSize
    )
    val paginationMetadata: StateFlow<PaginationMetadata> = _paginationMetadata

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _displayedPage = MutableStateFlow(1)
    val displayedPage: StateFlow<Int> = _displayedPage

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // StateFlow для текущего поискового запроса
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val availableGrades: List<Int> = AVAILABLE_GRADES // Список доступных классов

    private val _selectedGrades = MutableStateFlow<List<Int>>(emptyList())
    val selectedGrades: StateFlow<List<Int>> = _selectedGrades // Список выбранных классов

    // Используем Data Class FilterUiState для хранения всех UI фильтров
    private val _filtersUiState = MutableStateFlow(FilterUiState())
    val filtersUiState: StateFlow<FilterUiState> = _filtersUiState

    // TODO: StateFlow для доступных и выбранных предметов
    // private val _selectedSubjects = MutableStateFlow<List<Subject>>(emptyList())
    // val selectedSubjects: StateFlow<List<Subject>> = _selectedSubjects

    private val _errorState =
        MutableStateFlow<ErrorState>(ErrorState.NoError) // Начальное состояние: нет ошибки
    val errorState: StateFlow<ErrorState> = _errorState

    // Храним последние параметры загрузки для повтора
    private var lastLoadParams: LoadParams? = null

    init {
        println("OlympiadListViewModel: ViewModel hashcode: ${this.hashCode()}")

        // Наблюдаем за изменением pageSize и сбрасываем страницу на 1
        pageSize
            .drop(1)
            .onEach { newSize ->
                println("ViewModel: Page size changed to $newSize. Resetting page to 1.")
                _currentPage.value = 1
            }
            .launchIn(viewModelScope)


        // Combine Flow объединяет параметры, которые ИНИЦИИРУЮТ ЗАГРУЗКУ
        val loadParamsFlow = combine(
            _currentPage,
            pageSize,
            _searchQuery
                .debounce(300)
                .distinctUntilChanged(),
            _selectedGrades
            // TODO: Добавить Flow выбранных предметов
        ) { page, size, query, selectedGrades ->
            LoadParams(page, size, query, selectedGrades)
        }
            .distinctUntilChanged()

        // Наблюдаем за loadParamsFlow и вызываем loadData с новыми параметрами
        loadParamsFlow
            .onEach { params -> println("ViewModel: loadParamsFlow emitted: $params") }

            .onEach { params ->
                lastLoadParams = params // Сохраняем параметры перед вызовом loadData
            }
            .onEach { params -> println("ViewModel: Calling loadData with params: $params") }
            .onEach { params -> loadData(params) } // Вызываем loadData()
            .launchIn(viewModelScope)


    }


    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery // Обновляем только поисковый запрос
        _currentPage.value = 1 // Сбрасываем пагинацию, т.к. результаты поиска новые

        println("ViewModel: Search query changed. Filters preserved. Reset page to 1.")
    }


    /*
    // Изменили loadOlympiads, чтобы принимал размер страницы
    private fun loadOlympiads(page: Int = _currentPage.value, pageSize: Int = pageSize.value) {
        println("OlympiadListViewModel: Loading olympiads for page $page with pageSize $pageSize")
        viewModelScope.launch {
            _isLoading.value = true
            // Используем переданный или текущий размер страницы
            getPaginatedOlympiadsUseCase(page, pageSize)
                .catch { e -> // Добавим обработку ошибок
                    println("Error loading olympiads: ${e.message}")
                    _isLoading.value = false
                    // Можно показать сообщение об ошибке пользователю через отдельный StateFlow
                }
                .collectLatest { response ->
                    _olympiads.value = response.items
                    // Обновляем метаданные, включая pageSize из ответа (если API его возвращает)
                    // или используем текущий запрошенный 'pageSize'
                    _paginationMetadata.value = response.meta.copy(pageSize = pageSize) // Важно обновить pageSize в метаданных
                    _isLoading.value = false
                    println("OlympiadListViewModel: Loaded ${response.items.pageSize} items.")
                }
        }
    }
    OLD FUNCTION

     */

    fun onPageChanged(newPage: Int) {
        // Проверяем, что номер страницы корректен
        if (newPage >= 1 && newPage <= _paginationMetadata.value.totalPages && newPage != _currentPage.value) {
            // Обновляем значение текущей страницы
            _currentPage.value = newPage
            // Это изменение _currentPage автоматически увидит combine Flow и вызовет загрузку
        }
    }

    // --- Filter ---


    // Функция для изменения состояния выбранных классов
    fun onGradeFilterChanged(grade: Int, isSelected: Boolean) {
        val currentSelected = _selectedGrades.value.toMutableList()
        if (isSelected) {
            if (grade !in currentSelected) {
                currentSelected.add(grade)
            }
        } else {
            currentSelected.remove(grade)
        }
        // Обновляем StateFlow выбранных классов
        _selectedGrades.value = currentSelected.sorted() // Сортируем для стабильности

        // При изменении фильтров, сбрасываем пагинацию на первую страницу
        _currentPage.value = 1
        // combine Flow увидит изменение _selectedGrades и _currentPage и вызовет новую загрузку
    }


    // Функция для ИНИЦИАЛИЗАЦИИ состояния UI фильтров при открытии листа
    fun openFilterSheet() {
        // Копируем текущие активные фильтры в состояние UI
        _filtersUiState.value = FilterUiState(
            selectedGrades = _selectedGrades.value // Копируем выбранные классы
            // TODO: скопировать выбранные предметы
        )
        println("ViewModel: Filter sheet opened, UI state initialized with active filters: ${_filtersUiState.value}")
    }

    // Функция для ИЗМЕНЕНИЯ состояния фильтров ТОЛЬКО В UI листа
    fun onGradeFilterUiChanged(grade: Int, isSelected: Boolean) {
        val currentSelected = _filtersUiState.value.selectedGrades.toMutableList()
        if (isSelected) {
            if (grade !in currentSelected) {
                currentSelected.add(grade)
            }
        } else {
            currentSelected.remove(grade)
        }
        // Обновляем ТОЛЬКО StateFlow UI состояния
        _filtersUiState.value =
            _filtersUiState.value.copy(selectedGrades = currentSelected.sorted())
        println("ViewModel: UI filter changed: grade $grade, selected $isSelected. Current UI selected grades: ${_filtersUiState.value.selectedGrades}")
    }

    // Функция для ПРИМЕНЕНИЯ фильтров (при нажатии кнопки "Применить")
    fun applyFilters() {
        println("ViewModel: Apply filters button clicked. UI state: ${_filtersUiState.value}. Active state: ${_selectedGrades.value}")
        // Копируем выбранные фильтры из UI состояния в активное состояние фильтров
        val gradesChanged =
            _selectedGrades.value != _filtersUiState.value.selectedGrades // Проверяем, были ли изменения по классам
        // TODO: Проверить изменения по другим фильтрам

        _selectedGrades.value = _filtersUiState.value.selectedGrades
        // TODO: обновить активные предметы

        // Если какие-либо фильтры изменились, сбрасываем пагинацию на первую страницу
        if (gradesChanged /* || subjectsChanged */) {
            _currentPage.value = 1
            println("ViewModel: Filters changed, resetting page to 1.")
        }

        println("ViewModel: Filters applied. New active selected grades: ${_selectedGrades.value}. New active page: ${_currentPage.value}")
    }

    // Функция для СБРОСА состояния фильтров ТОЛЬКО В UI листа (например, кнопка "Сбросить" в листе)
    fun resetFiltersUiState() {
        _filtersUiState.value = FilterUiState()
        println("ViewModel: Filter UI state reset.")
    }

    fun discardFilterChanges() {
        openFilterSheet()
        println("ViewModel: Filter UI changes discarded. Reverted UI state to active filters: ${_filtersUiState.value}")
    }

    fun resetAndApplyFilters() {
        println("ViewModel: Reset and apply filters button clicked.")
        _selectedGrades.value = emptyList()
        // TODO: сбросить активные предметы

        // Сбрасываем UI состояние фильтров к дефолту, чтобы при следующем открытии они были чистыми
        _filtersUiState.value = FilterUiState()
        _currentPage.value = 1

        println("ViewModel: Filters reset and applied. Active selected grades: ${_selectedGrades.value}. Active page: ${_currentPage.value}")
    }


    fun refreshData() {
        _currentPage.value = 1
        _displayedPage.value = 1
        _searchQuery.value = ""
        _selectedGrades.value = emptyList()

        _filtersUiState.value = FilterUiState()
        println("ViewModel: Refresh data, reset active filters and UI state.")
        // TODO: Сбрасывать выбранные предметы
    }

    fun onRetryClicked() {
        println("ViewModel: Retry button clicked. Attempting to reload last requested data.")
        // Используем последние сохраненные параметры для повторного вызова loadData
        lastLoadParams?.let { params ->
            loadData(params) // Перезапускаем Flow загрузки
        }
        // Если lastLoadParams == null (что маловероятно, если ошибка произошла после первой загрузки),
        // можно рассмотреть вызов refreshData() или loadData(LoadParams(1, pageSize.value, ...))
    }

    private fun loadData(params: LoadParams) {
        viewModelScope.launch {
            println("ViewModel: Inside loadData for page ${params.page}, query '${params.query}', grades ${params.selectedGrades}")

            _isLoading.value = true // Ставим true в начале загрузки
            _errorState.value = ErrorState.NoError // Сбрасываем предыдущую ошибку


            // Use Case, который возвращает Flow<Result<...>>
            getPaginatedOlympiadsUseCase(
                page = params.page,
                pageSize = params.pageSize,
                query = params.query,
                selectedGrades = params.selectedGrades
            )

                // Обрабатываем РЕЗУЛЬТАТ (успех или ошибка) в блоке onEach
                // Блок catch здесь НЕ НУЖЕН, потому что ошибки представлены как Result.failure
                .onEach { result -> // result типа Result<PaginatedResponse<Olympiad>>
                    println("ViewModel: loadData received result.")

                    _isLoading.value =
                        false // Снимаем индикатор загрузки (независимо от успеха/ошибки)

                    result.fold( // Используем fold для обработки Result
                        onSuccess = { finalPaginatedResponse -> // finalPaginatedResponse - это PaginatedResponse<Olympiad> из Result.success
                            println("ViewModel: loadData success. Processing response for page ${finalPaginatedResponse.meta.currentPage}.")

                            // Обновляем UI состояние при успехе
                            _olympiads.value = finalPaginatedResponse.items
                            _paginationMetadata.value = finalPaginatedResponse.meta
                            _displayedPage.value =
                                finalPaginatedResponse.meta.currentPage // Обновляем отображаемую страницу

                            _errorState.value =
                                ErrorState.NoError // Очищаем состояние ошибки при успешной загрузке

                            println("ViewModel: loadData success. Loaded ${finalPaginatedResponse.items.size} items for page ${finalPaginatedResponse.meta.currentPage}.")
                        },
                        onFailure = { e -> // e - это Throwable из Result.failure
                            println("ViewModel: loadData failure. Exception: ${e.message}")
                            println("ViewModel: Caught exception type: ${e::class.java.name}") // ЛОГ ТИПА ИСКЛЮЧЕНИЯ

                            // Обновляем UI состояние при ошибке
                            _olympiads.value = emptyList() // Очищаем список
                            _paginationMetadata.value = PaginationMetadata(
                                totalItems = 0,
                                totalPages = 1,
                                currentPage = params.page,
                                pageSize = params.pageSize
                            ) // Сбрасываем метаданные

                            // Устанавливаем состояние ошибки в зависимости от типа исключения
                            _errorState.value = when (e) {
                                is IOException -> ErrorState.NetworkError // Ошибки ввода-вывода (сеть)
                                is HttpException -> ErrorState.ServerError(e.message()) // Ошибки HTTP (сервер)
                                is IllegalStateException -> ErrorState.ServerError(e.message) // Ошибка данных из репозитория
                                else -> ErrorState.ServerError(
                                    e.message ?: "Неизвестная ошибка загрузки"
                                ) // Остальные ошибки
                            }
                        }
                    )
                }
                .launchIn(viewModelScope)
        }
    }
}