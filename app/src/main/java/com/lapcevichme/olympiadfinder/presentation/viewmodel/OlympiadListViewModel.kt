package com.lapcevichme.olympiadfinder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lapcevichme.olympiadfinder.data.local.DEFAULT_PAGE_SIZE
import com.lapcevichme.olympiadfinder.data.repository.mock.PaginationMetadata // <- Убедись, что используется твой настоящий, а не mock
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.usecases.GetPaginatedOlympiadsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.GetPageSizePreferenceUseCase // <-- ИМПОРТ
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimateListItemsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.settings.animations.GetAnimatePageTransitionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.* // Импортируем нужные операторы flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OlympiadListViewModel @Inject constructor(
    // private val getAllOlympiadsUseCase: GetAllOlympiadsUseCase, // Убрал, если не используется
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
            initialValue = DEFAULT_PAGE_SIZE // Начальное значение по умолчанию
        )

    // StateFlow для animatePageTransitions
    val animatePageTransitions: StateFlow<Boolean> = getAnimatePageTransitionsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true // Значение по умолчанию
        )

    // StateFlow для animateListItems
    val animateListItems: StateFlow<Boolean> = getAnimateListItemsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true // Значение по умолчанию
        )



    // _paginationMetadata теперь не хранит изменяемый pageSize, он берется из pageSize StateFlow
    private val _paginationMetadata = MutableStateFlow(
        PaginationMetadata(totalItems = 0, totalPages = 1, currentPage = 1, pageSize = pageSize.value) // Используем начальное значение pageSize
    )
    val paginationMetadata: StateFlow<PaginationMetadata> = _paginationMetadata

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Флаг убран, так как логика загрузки изменилась
    // private var hasLoadedInitialData = false

    init {
        println("OlympiadListViewModel: ViewModel hashcode: ${this.hashCode()}")
        // Запускаем начальную загрузку
        loadOlympiads(page = _currentPage.value, size = pageSize.value)

        // Наблюдаем за изменениями настройки pageSize
        viewModelScope.launch {
            pageSize
                .drop(1) // Пропускаем начальное значение, так как оно уже учтено в init
                .distinctUntilChanged() // Реагируем только на реальные изменения
                .collectLatest { newSize ->
                    println("OlympiadListViewModel: Page size preference changed to $newSize. Reloading.")
                    // Сбрасываем на первую страницу и перезагружаем с новым размером
                    _currentPage.value = 1
                    loadOlympiads(page = 1, size = newSize)
                }
        }
    }

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


    fun onPageChanged(newPage: Int) {
        _currentPage.value = newPage
        // Загружаем новую страницу с ТЕКУЩИМ размером страницы
        loadOlympiads(page = newPage, size = pageSize.value)
    }

    // ЭТА ФУНКЦИЯ БОЛЬШЕ НЕ НУЖНА ИЗВНЕ, УДАЛЯЕМ
    /*
    fun onPageSizeChanged(newSize: Int) {
        // _paginationMetadata.value = _paginationMetadata.value.copy(pageSize = newSize) // Устарело
        _currentPage.value = 1
        loadOlympiads()
    }
    */

    fun refreshData() {
        // Сбрасываем на первую страницу и перезагружаем с текущим размером страницы
        _currentPage.value = 1
        loadOlympiads(page = 1, size = pageSize.value)
    }
}