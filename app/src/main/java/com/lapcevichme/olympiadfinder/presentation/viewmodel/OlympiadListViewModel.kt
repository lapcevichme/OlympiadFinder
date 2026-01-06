package com.lapcevichme.olympiadfinder.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lapcevichme.olympiadfinder.data.local.PreferencesSettingsDataStore.Companion.DEFAULT_PAGE_SIZE
import com.lapcevichme.olympiadfinder.domain.model.AppError
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginationMetadata
import com.lapcevichme.olympiadfinder.domain.model.Resource
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.domain.usecases.GetAvailableSubjectsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.GetPaginatedOlympiadsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.GetPageSizePreferenceUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimateListItemsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimatePageTransitionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
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
import javax.inject.Inject

/**
 * ViewModel для экрана списка олимпиад.
 * Отвечает за загрузку, фильтрацию, пагинацию олимпиад,
 * а также управление состоянием UI и ошибок.
 *
 * @param getPaginatedOlympiadsUseCase Use Case для получения пагинированного списка олимпиад.
 * @param getAvailableSubjectsUseCase Use Case для получения списка доступных предметов.
 * @param getPageSizePreferenceUseCase Use Case для получения размера страницы из настроек.
 * @param getAnimatePageTransitionsUseCase Use Case для получения настройки анимации переходов между страницами.
 * @param getAnimateListItemsUseCase Use Case для получения настройки анимации элементов списка.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class OlympiadListViewModel @Inject constructor(
    private val getPaginatedOlympiadsUseCase: GetPaginatedOlympiadsUseCase,
    private val getAvailableSubjectsUseCase: GetAvailableSubjectsUseCase,
    getPageSizePreferenceUseCase: GetPageSizePreferenceUseCase,
    getAnimatePageTransitionsUseCase: GetAnimatePageTransitionsUseCase,
    getAnimateListItemsUseCase: GetAnimateListItemsUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "OlympiadListViewModel"
        val AVAILABLE_GRADES = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
    }

    /** [StateFlow] списка олимпиад, отображаемых в UI. */
    private val _olympiads = MutableStateFlow<List<Olympiad>>(emptyList())
    val olympiads: StateFlow<List<Olympiad>> = _olympiads

    /** [StateFlow] размера страницы, полученный из настроек пользователя. */
    private val pageSize: StateFlow<Int> = getPageSizePreferenceUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = DEFAULT_PAGE_SIZE
        )

    /** [StateFlow] состояния включения/выключения анимации переходов между страницами. */
    val animatePageTransitions: StateFlow<Boolean> = getAnimatePageTransitionsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    /** [StateFlow] состояния включения/выключения анимации элементов списка. */
    val animateListItems: StateFlow<Boolean> = getAnimateListItemsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    /** [StateFlow] метаданных пагинации (общее количество элементов, страниц и т.д.). */
    private val _paginationMetadata = MutableStateFlow(
        PaginationMetadata(
            totalItems = 0,
            totalPages = 1,
            currentPage = 1,
            pageSize = pageSize.value
        )
    )
    val paginationMetadata: StateFlow<PaginationMetadata> = _paginationMetadata

    /** [StateFlow] текущей активной страницы запроса. */
    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    /** [StateFlow] страницы, которая фактически отображается в UI (для сброса скролла). */
    private val _displayedPage = MutableStateFlow(1)
    val displayedPage: StateFlow<Int> = _displayedPage

    /** [StateFlow] индикатора загрузки данных. */
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** [StateFlow] текущего поискового запроса пользователя. */
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    /** Список доступных классов для фильтрации (константа). */
    val availableGrades: List<Int> = AVAILABLE_GRADES

    /** [StateFlow] активно выбранных классов (активные фильтры, передаются в Use Case). */
    private val _selectedGrades = MutableStateFlow<List<Int>>(emptyList())
    val selectedGrades: StateFlow<List<Int>> = _selectedGrades

    /** [StateFlow] для хранения состояния UI фильтров (для Bottom Sheet). */
    private val _filtersUiState = MutableStateFlow(FilterUiState())
    val filtersUiState: StateFlow<FilterUiState> = _filtersUiState

    /** [StateFlow] для списка доступных предметов (получаются с сервера). */
    private val _availableSubjects = MutableStateFlow<Resource<List<Subject>>>(Resource.loading())
    val availableSubjects: StateFlow<Resource<List<Subject>>> = _availableSubjects

    /** [StateFlow] для активно выбранных предметов (активные фильтры, передаются в Use Case). */
    private val _selectedSubjects = MutableStateFlow<List<Long>>(emptyList())
    val selectedSubjects: StateFlow<List<Long>> = _selectedSubjects

    /** [StateFlow] для состояния ошибки, отображаемой в UI. Теперь хранит [AppError] или `null`. */
    private val _errorState = MutableStateFlow<AppError?>(null)
    val errorState: StateFlow<AppError?> = _errorState

    /** Хранит последние параметры загрузки для повтора в случае ошибки. */
    private var lastLoadParams: LoadParams? = null

    init {
        Log.i(TAG, "OlympiadListViewModel created with hashcode: ${this.hashCode()}")

        // Наблюдаем за изменением pageSize и сбрасываем страницу на 1
        pageSize
            .drop(1) // Пропускаем первое значение (initialValue)
            .onEach { newSize ->
                Log.d(TAG, "Page size changed to $newSize. Resetting page to 1.")
                _currentPage.value = 1
            }
            .launchIn(viewModelScope)


        // Combine Flow объединяет параметры, которые ИНИЦИИРУЮТ ЗАГРУЗКУ
        val loadParamsFlow = combine(
            _currentPage,
            pageSize,
            _searchQuery
                .debounce(300) // Задержка для поискового запроса
                .distinctUntilChanged(), // Только если запрос изменился
            _selectedGrades,
            _selectedSubjects
        ) { page, size, query, selectedGrades, selectedSubjects ->
            LoadParams(page, size, query, selectedGrades, selectedSubjects)
        }
            .distinctUntilChanged() // Только если параметры загрузки изменились

        // Наблюдаем за loadParamsFlow и вызываем loadData с новыми параметрами
        loadParamsFlow
            .onEach { params ->
                Log.v(
                    TAG,
                    "loadParamsFlow emitted: $params"
                )
            } // Подробный лог эмиссии параметров

            .onEach { params ->
                lastLoadParams = params // Сохраняем параметры перед вызовом loadData
            }
            .onEach { params ->
                Log.d(
                    TAG,
                    "Calling loadData with params: $params"
                )
            } // Лог вызова loadData
            .onEach { params -> loadData(params) } // Вызываем loadData()
            .launchIn(viewModelScope)

        // Загружаем доступные предметы при инициализации ViewModel
        loadAvailableSubjects()
    }


    /**
     * Обрабатывает изменение поискового запроса пользователя.
     * Сбрасывает текущую страницу на первую и запускает новый запрос.
     * @param newQuery Новый поисковый запрос.
     */
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery // Обновляем только поисковый запрос
        _currentPage.value = 1 // Сбрасываем пагинацию, т.к. результаты поиска новые

        Log.d(TAG, "Search query changed to '$newQuery'. Filters preserved. Reset page to 1.")
    }

    /**
     * Обрабатывает изменение текущей страницы пагинации.
     * @param newPage Новый номер страницы.
     */
    fun onPageChanged(newPage: Int) {
        // Проверяем, что номер страницы корректен
        if (newPage >= 1 && newPage <= _paginationMetadata.value.totalPages && newPage != _currentPage.value) {
            // Обновляем значение текущей страницы
            _currentPage.value = newPage
            // Это изменение _currentPage автоматически увидит combine Flow и вызовет загрузку
            Log.d(TAG, "Page changed to: $newPage")
        } else {
            Log.w(
                TAG,
                "Attempted to change to invalid page: $newPage. Current page: ${_currentPage.value}, Total pages: ${_paginationMetadata.value.totalPages}"
            )
        }
    }

    // --- Функции для управления фильтрами ---

    /**
     * Инициализирует состояние UI фильтров при открытии Bottom Sheet.
     * Копирует текущие активные фильтры в состояние UI.
     */
    fun openFilterSheet() {
        _filtersUiState.value = FilterUiState(
            selectedGrades = _selectedGrades.value, // Копируем выбранные классы
            selectedSubjects = _selectedSubjects.value // Копируем выбранные предметы
        )
        Log.d(
            TAG,
            "Filter sheet opened, UI state initialized with active filters: ${_filtersUiState.value}"
        )
    }

    /**
     * Обрабатывает изменение состояния выбора класса в UI фильтров (только для UI).
     * @param grade Класс, который изменил состояние.
     * @param isSelected Новое состояние выбора (true - выбран, false - не выбран).
     */
    fun onGradeFilterUiChanged(grade: Int, isSelected: Boolean) {
        val currentSelected = _filtersUiState.value.selectedGrades.toMutableList()
        if (isSelected) {
            if (grade !in currentSelected) {
                currentSelected.add(grade)
            }
        } else {
            currentSelected.remove(grade)
        }
        _filtersUiState.value =
            _filtersUiState.value.copy(selectedGrades = currentSelected.sorted())
        Log.v(
            TAG,
            "UI filter changed: grade $grade, selected $isSelected. Current UI selected grades: ${_filtersUiState.value.selectedGrades}"
        )
    }

    /**
     * Обрабатывает изменение состояния выбора предмета в UI фильтров (только для UI).
     * @param subjectId ID предмета, который изменил состояние.
     * @param isSelected Новое состояние выбора (true - выбран, false - не выбран).
     */
    fun onSubjectFilterUiChanged(subjectId: Long, isSelected: Boolean) {
        val currentSelected = _filtersUiState.value.selectedSubjects.toMutableList()
        if (isSelected) {
            if (subjectId !in currentSelected) {
                currentSelected.add(subjectId)
            }
        } else {
            currentSelected.remove(subjectId)
        }
        _filtersUiState.value =
            _filtersUiState.value.copy(selectedSubjects = currentSelected.sorted())
        Log.v(
            TAG,
            "UI filter changed: subject $subjectId, selected $isSelected. Current UI selected subjects: ${_filtersUiState.value.selectedSubjects}"
        )
    }

    /**
     * Применяет выбранные фильтры из UI состояния в активное состояние.
     * Если фильтры изменились, сбрасывает пагинацию на первую страницу.
     */
    fun applyFilters() {
        Log.d(
            TAG,
            "Apply filters button clicked. UI state: ${_filtersUiState.value}. Active state: Grades: ${_selectedGrades.value}, Subjects: ${_selectedSubjects.value}"
        )

        val gradesChanged = _selectedGrades.value != _filtersUiState.value.selectedGrades
        val subjectsChanged = _selectedSubjects.value != _filtersUiState.value.selectedSubjects

        _selectedGrades.value = _filtersUiState.value.selectedGrades
        _selectedSubjects.value = _filtersUiState.value.selectedSubjects

        if (gradesChanged || subjectsChanged) {
            _currentPage.value = 1
            Log.i(
                TAG,
                "Filters changed, resetting page to 1. New active filters: grades=${_selectedGrades.value}, subjects=${_selectedSubjects.value}"
            )
        } else {
            Log.d(TAG, "Filters applied, no changes detected.")
        }
        Log.d(
            TAG,
            "Filters applied. New active selected grades: ${_selectedGrades.value}. New active selected subjects: ${_selectedSubjects.value}. New active page: ${_currentPage.value}"
        )
    }

    /**
     * Сбрасывает состояние фильтров только в UI листа (без применения).
     */
    fun resetFiltersUiState() {
        _filtersUiState.value = FilterUiState()
        Log.d(TAG, "Filter UI state reset.")
    }

    /**
     * Отменяет изменения фильтров в UI и возвращает их к активному состоянию.
     */
    fun discardFilterChanges() {
        openFilterSheet() // Возвращаем UI состояние к активным фильтрам
        Log.d(
            TAG,
            "Filter UI changes discarded. Reverted UI state to active filters: ${_filtersUiState.value}"
        )
    }

    /**
     * Сбрасывает все активные фильтры и UI состояние фильтров, затем применяет изменения.
     */
    fun resetAndApplyFilters() {
        Log.d(TAG, "Reset and apply filters button clicked.")
        _selectedGrades.value = emptyList()
        _selectedSubjects.value = emptyList()

        _filtersUiState.value = FilterUiState() // Сбрасываем UI состояние фильтров к дефолту
        _currentPage.value = 1 // Сбрасываем пагинацию на первую страницу

        Log.i(
            TAG,
            "Filters reset and applied. Active selected grades: ${_selectedGrades.value}. Active selected subjects: ${_selectedSubjects.value}. Active page: ${_currentPage.value}"
        )
    }

    /**
     * Сбрасывает все фильтры, поисковый запрос и пагинацию, затем инициирует новую загрузку данных.
     */
    fun refreshData() {
        _currentPage.value = 1
        _displayedPage.value = 1
        _searchQuery.value = ""
        _selectedGrades.value = emptyList()
        _selectedSubjects.value = emptyList()

        _filtersUiState.value = FilterUiState()
        Log.i(TAG, "Refresh data: reset active filters, UI state, search query, and pagination.")
    }

    /**
     * Повторяет последнюю загрузку данных при нажатии кнопки "Повторить".
     */
    fun onRetryClicked() {
        Log.d(TAG, "Retry button clicked. Attempting to reload last requested data.")
        lastLoadParams?.let { params ->
            loadData(params) // Перезапускаем Flow загрузки
        } ?: Log.w(
            TAG,
            "lastLoadParams is null, cannot retry."
        ) // Предупреждение, если нет параметров для повтора
    }

    /**
     * Загружает пагинированные олимпиады с учетом текущих параметров фильтрации и поиска.
     * @param params Параметры загрузки (страница, размер страницы, запрос, выбранные классы и предметы).
     */
    private fun loadData(params: LoadParams) {
        viewModelScope.launch {
            Log.d(
                TAG,
                "Inside loadData for page ${params.page}, query '${params.query}', grades ${params.selectedGrades}, subjects ${params.selectedSubjects}"
            )

            _isLoading.value = true // Ставим true в начале загрузки
            _errorState.value = null // Сбрасываем предыдущую ошибку


            getPaginatedOlympiadsUseCase(
                page = params.page,
                pageSize = params.pageSize,
                query = params.query,
                selectedGrades = params.selectedGrades,
                selectedSubjects = params.selectedSubjects
            )
                .onEach { result -> // result типа Resource<PaginatedResponse<Olympiad>>
                    Log.v(TAG, "loadData received result: $result")

                    _isLoading.value =
                        false // Снимаем индикатор загрузки (независимо от успеха/ошибки)

                    result.fold( // Используем fold для обработки Resource
                        onSuccess = { finalPaginatedResponse -> // finalPaginatedResponse - это PaginatedResponse<Olympiad> из Resource.Success
                            Log.i(
                                TAG,
                                "loadData success. Processing response for page ${finalPaginatedResponse.meta.currentPage}."
                            )

                            _olympiads.value = finalPaginatedResponse.items
                            _paginationMetadata.value = finalPaginatedResponse.meta
                            _displayedPage.value = finalPaginatedResponse.meta.currentPage

                            _errorState.value =
                                null // Очищаем состояние ошибки при успешной загрузке

                            Log.i(
                                TAG,
                                "loadData success. Loaded ${finalPaginatedResponse.items.size} items for page ${finalPaginatedResponse.meta.currentPage}."
                            )
                        },
                        onFailure = { appError -> // Теперь получаем AppError напрямую из репозитория
                            Log.e(TAG, "loadData failure. AppError: $appError")

                            _olympiads.value = emptyList() // Очищаем список
                            _paginationMetadata.value = PaginationMetadata(
                                totalItems = 0,
                                totalPages = 1,
                                currentPage = params.page,
                                pageSize = params.pageSize
                            )

                            _errorState.value = appError // Устанавливаем AppError напрямую
                        },
                        onLoading = {
                            Log.v(TAG, "loadData is in loading state.")
                        }
                    )
                }
                .launchIn(viewModelScope)
        }
    }

    /**
     * Загружает список доступных предметов с сервера.
     */
    private fun loadAvailableSubjects() {
        viewModelScope.launch {
            Log.d(TAG, "Loading available subjects...")
            _availableSubjects.value = Resource.loading() // Устанавливаем состояние загрузки

            val result = getAvailableSubjectsUseCase() // Вызываем Use Case

            _availableSubjects.value = result // Обновляем StateFlow результатом
            result.fold(
                onSuccess = { subjects ->
                    Log.i(TAG, "Successfully loaded ${subjects.size} available subjects.")
                },
                onFailure = { appError -> // Получаем AppError напрямую из репозитория
                    Log.e(TAG, "Failed to load available subjects: $appError")
                    // Здесь можно установить _errorState.value, если ошибка загрузки предметов
                    // должна влиять на общий экран, но для фильтров, возможно, достаточно
                    // просто показать пустое состояние или сообщение об ошибке внутри листа.
                },
                onLoading = {
                    Log.v(TAG, "Available subjects are in loading state.")
                }
            )
        }
    }
}
