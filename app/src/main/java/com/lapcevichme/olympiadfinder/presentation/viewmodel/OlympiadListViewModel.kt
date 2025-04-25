package com.lapcevichme.olympiadfinder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lapcevichme.olympiadfinder.data.local.DEFAULT_PAGE_SIZE
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginatedResponse
import com.lapcevichme.olympiadfinder.domain.model.PaginationMetadata
import com.lapcevichme.olympiadfinder.domain.usecases.GetPaginatedOlympiadsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.GetPageSizePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimateListItemsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimatePageTransitionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
        PaginationMetadata(totalItems = 0, totalPages = 1, currentPage = 1, pageSize = pageSize.value) // Используем начальное значение pageSize
    )
    val paginationMetadata: StateFlow<PaginationMetadata> = _paginationMetadata

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _isLoading = MutableStateFlow(false)
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

    init {
        println("OlympiadListViewModel: ViewModel hashcode: ${this.hashCode()}")

        combine(
            _currentPage, // Реагируем на смену страницы
            pageSize,     // Реагируем на изменение размера страницы (из настроек)
            _searchQuery  // Реагируем на изменение поискового запроса
                .debounce(300) // Ждем 300ms после последнего символа перед реакцией
                .distinctUntilChanged(), // Пропускаем повторные одинаковые запросы (после debounce)
            _selectedGrades
        ) { page, size, query, selectedGrades  ->
            // Возвращаем объединенное значение в виде Triple.
            // Эти page, size, query будут доступны в flatMapLatest
            LoadParams(page, size, query, selectedGrades /*, selectedSubjects */)
        }
            .distinctUntilChanged()

            .onEach { _isLoading.value = true } // Просто ставим загрузку в начале каждого запроса

            .flatMapLatest { params -> // <-- ИСПРАВЛЕНИЕ: Принимаем Data Class LoadParams
                println("OlympiadListViewModel: Triggering load for page ${params.page}, size ${params.size}, query '${params.query}', grades ${params.selectedGrades}")

                // Вызываем Use Case, передавая параметры из Data Class
                // Use Case ожидает query: String?, но params.query: String тоже подходит
                // Use Case ожидает selectedGrades: List<Int>, что соответствует params.selectedGrades
                getPaginatedOlympiadsUseCase(
                    page = params.page,
                    pageSize = params.size,
                    query = params.query, // <-- Передаем query из Data Class
                    selectedGrades = params.selectedGrades // <-- Передаем selectedGrades из Data Class
                    // TODO: Передать selectedSubjects из Data Class
                )
                    // <-- ИСПРАВЛЕНИЕ: Проносим через pipeline Use Case НЕ ТОЛЬКО ответ, НО И ОРИГИНАЛЬНЫЕ ПАРАМЕТРЫ (Data Class LoadParams)
                    // Создаем Pair: (ответ Use Case, оригинальные параметры LoadParams)
                    .map { response -> Pair(response, params) } // <-- Эмитируем Pair<PaginatedResponse<Olympiad>, LoadParams>

                    .catch { e -> // Обработка ошибок Use Case Flow
                        println("Error loading olympiads: ${e.message}")
                        // В случае ошибки: эмитируем пустой ответ и метаданные,
                        // ПРОНОСИМ через этот catch ОРИГИНАЛЬНЫЕ ПАРАМЕТРЫ LoadParams
                        val emptyResponse = PaginatedResponse(emptyList<Olympiad>(), PaginationMetadata(0, 1, params.page, params.size))
                        // <-- ИСПРАВЛЕНИЕ: Эмитируем Pair с пустым ответом и оригинальными параметрами LoadParams
                        emit(Pair(emptyResponse, params))
                        // Можно также перебросить исключение, если оно должно обрабатываться дальше
                        // throw e
                    }
            }
            // onEach выполняется для каждого значения, успешно эмитированного из flatMapLatest (это теперь Triple!)
            .onEach { (response, params) -> // <-- ИСПРАВЛЕНИЕ: РАЗБИРАЕМ Pair, ПОЛУЧАЕМ ответ И ОРИГИНАЛЬНЫЕ ПАРАМЕТРЫ LoadParams
                // Обрабатываем успешный ответ или пустой ответ после catch
                _olympiads.value = response.items // Обновляем список олимпиад
                // Обновляем метаданные пагинации
                // Используем метаданные из ответа (response.meta), но переписываем currentPage и pageSize
                // на те, которые пришли в оригинальных параметрах (params.page и params.size).
                _paginationMetadata.value = response.meta.copy(currentPage = params.page, pageSize = params.size) // <-- ИСПРАВЛЕНИЕ: Используем page и size из Data Class
                _isLoading.value = false // Снимаем индикатор загрузки
                println("OlympiadListViewModel: Loaded ${response.items.size} items for page ${params.page}.")
            }
            .launchIn(viewModelScope) // Запускаем сбор всего Flow
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery // Обновляем только поисковый запрос
        _currentPage.value = 1 // Сбрасываем пагинацию, т.к. результаты поиска новые
        println("ViewModel: Search query changed. Filters preserved. Reset page to 1.")
    }


    /*
    // Изменили loadOlympiads, чтобы принимал размер страницы
    private fun loadOlympiads(page: Int = _currentPage.value, size: Int = pageSize.value) {
        println("OlympiadListViewModel: Loading olympiads for page $page with size $size")
        viewModelScope.launch {
            _isLoading.value = true
            // Используем переданный или текущий размер страницы
            getPaginatedOlympiadsUseCase(page, size)
                .catch { e -> // Добавим обработку ошибок
                    println("Error loading olympiads: ${e.message}")
                    _isLoading.value = false
                    // Можно показать сообщение об ошибке пользователю через отдельный StateFlow
                }
                .collectLatest { response ->
                    _olympiads.value = response.items
                    // Обновляем метаданные, включая pageSize из ответа (если API его возвращает)
                    // или используем текущий запрошенный 'size'
                    _paginationMetadata.value = response.meta.copy(pageSize = size) // Важно обновить pageSize в метаданных
                    _isLoading.value = false
                    println("OlympiadListViewModel: Loaded ${response.items.size} items.")
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


    // НОВОЕ: Функция для ИНИЦИАЛИЗАЦИИ состояния UI фильтров при открытии листа
    fun openFilterSheet() {
        // Копируем текущие активные фильтры в состояние UI
        _filtersUiState.value = FilterUiState(
            selectedGrades = _selectedGrades.value // Копируем выбранные классы
            // TODO: скопировать выбранные предметы
        )
        println("ViewModel: Filter sheet opened, UI state initialized with active filters: ${_filtersUiState.value}")
    }

    // НОВОЕ: Функция для ИЗМЕНЕНИЯ состояния фильтров ТОЛЬКО В UI листа
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
        _filtersUiState.value = _filtersUiState.value.copy(selectedGrades = currentSelected.sorted())
        println("ViewModel: UI filter changed: grade $grade, selected $isSelected. Current UI selected grades: ${_filtersUiState.value.selectedGrades}")
    }

    // Функция для ПРИМЕНЕНИЯ фильтров (при нажатии кнопки "Применить")
    fun applyFilters() {
        println("ViewModel: Apply filters button clicked. UI state: ${_filtersUiState.value}. Active state: ${_selectedGrades.value}")
        // Копируем выбранные фильтры из UI состояния в активное состояние фильтров
        val gradesChanged = _selectedGrades.value != _filtersUiState.value.selectedGrades // Проверяем, были ли изменения по классам
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
        _searchQuery.value = ""
        _selectedGrades.value = emptyList()

        _filtersUiState.value = FilterUiState()
        println("ViewModel: Refresh data, reset active filters and UI state.")
        // TODO: Сбрасывать выбранные предметы
    }
}