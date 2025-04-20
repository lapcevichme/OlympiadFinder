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

    init {
        println("OlympiadListViewModel: ViewModel hashcode: ${this.hashCode()}")

        combine(
            _currentPage, // Реагируем на смену страницы
            pageSize,     // Реагируем на изменение размера страницы (из настроек)
            _searchQuery  // Реагируем на изменение поискового запроса
                .debounce(300) // Ждем 300ms после последнего символа перед реакцией
                .distinctUntilChanged() // Пропускаем повторные одинаковые запросы (после debounce)
            // TODO: Добавить Flow выбранных фильтров сюда
        ) { page, size, query ->
            // Возвращаем объединенное значение в виде Triple.
            // Эти page, size, query будут доступны в flatMapLatest
            Triple(page, size, query)
        }
            .onEach { _isLoading.value = true } // Просто ставим загрузку в начале каждого запроса

            .flatMapLatest { (page, size, query) ->
                // flatMapLatest: отменяет предыдущий запущенный Flow, если приходит новое значение из combine
                println("OlympiadListViewModel: Triggering load for page $page, size $size, query '$query'")

                // Вызываем Use Case, передавая все параметры
                // У Use Case возвращаемый тип Flow<PaginatedResponse<Olympiad>>
                getPaginatedOlympiadsUseCase(page, size, query /*, selectedSubjects, selectedGrades */)
                    // Внутри flatMapLatest МЫ СОХРАНЯЕМ page И size ИЗ ВХОДНОГО Triple
                    // И ВОЗВРАЩАЕМ ИХ ВМЕСТЕ С ОТВЕТОМ ИЗ Use Case В НОВОМ Triple
                    .map { response -> Triple(response, page, size) } // <-- Создаем новое Triple: (ответ, page, size)
                    .catch { e -> // Обработка ошибок внутри Use Case Flow
                        println("Error loading olympiads: ${e.message}")
                        // В случае ошибки: эмитируем пустой ответ и метаданные,
                        // ПРОНОСИМ через этот catch оригинальные page и size
                        val emptyResponse = PaginatedResponse(emptyList<Olympiad>(), PaginationMetadata(0, 1, page, size))
                        emit(Triple(emptyResponse, page, size)) // <-- Эмитируем Triple: (пустой ответ, page, size)
                        // Можно также перебросить исключение, если оно должно обрабатываться дальше
                        // throw e
                    }
            }
            // onEach выполняется для каждого значения, успешно эмитированного из flatMapLatest (это теперь Triple!)
            .onEach { (response, page, size) -> // <-- РАЗБИРАЕМ Triple, ПОЛУЧАЕМ page И size
                // Обрабатываем успешный ответ или пустой ответ после catch
                _olympiads.value = response.items // Обновляем список олимпиад
                // Обновляем метаданные пагинации
                // Используем метаданные из ответа (response.meta), но переписываем currentPage и pageSize
                // на те, которые пришли в этом Triple (page и size).
                _paginationMetadata.value = response.meta.copy(currentPage = page, pageSize = size)
                _isLoading.value = false // Снимаем индикатор загрузки
                println("OlympiadListViewModel: Loaded ${response.items.size} items for page $page.")
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(newQuery: String) {
        // Обновляем значение поискового запроса в StateFlow
        _searchQuery.value = newQuery
        // При изменении поискового запроса, всегда сбрасываем пагинацию на первую страницу.
        // Это автоматически инициирует новую загрузку через combine Flow.
        _currentPage.value = 1
        // TODO: Если добавлять фильтры, здесь также нужно решить:
        // Сбрасывать выбранные фильтры при новом текстовом запросе? (Чаще всего - да)
        // Или искать внутри уже примененных фильтров? (Реже, сложнее)
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

    fun refreshData() {
        _currentPage.value = 1
        _searchQuery.value = ""
    }
}