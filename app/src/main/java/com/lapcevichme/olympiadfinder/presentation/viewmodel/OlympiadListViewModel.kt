package com.lapcevichme.olympiadfinder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lapcevichme.olympiadfinder.data.repository.mock.PaginationMetadata
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.usecases.GetAllOlympiadsUseCase
import com.lapcevichme.olympiadfinder.domain.usecases.GetPaginatedOlympiadsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OlympiadListViewModel @Inject constructor(
    private val getAllOlympiadsUseCase: GetAllOlympiadsUseCase,
    private val getPaginatedOlympiadsUseCase: GetPaginatedOlympiadsUseCase
) : ViewModel() {

    private val _olympiads = MutableStateFlow<List<Olympiad>>(emptyList())
    val olympiads: StateFlow<List<Olympiad>> = _olympiads

    private val _paginationMetadata = MutableStateFlow(
        PaginationMetadata(totalItems = 0, totalPages = 1, currentPage = 1, pageSize = 10)
    )
    val paginationMetadata: StateFlow<PaginationMetadata> = _paginationMetadata

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var hasLoadedInitialData = false

    init {
        println("OlympiadListViewModel: ViewModel hashcode: ${this.hashCode()}")
        loadOlympiads()
    }

    fun loadOlympiads() {
        println("OlympiadListViewModel: started loading olympiads")
        if (hasLoadedInitialData && _currentPage.value == 1 && _olympiads.value.isNotEmpty()) {
            println("OlympiadListViewModel: loadOlympiads() skipped - data already loaded.")
            return
        }
        println("OlympiadListViewModel: Loading olympiads for page ${_currentPage.value}")

        viewModelScope.launch {
            _isLoading.value = true
            getPaginatedOlympiadsUseCase(_currentPage.value, _paginationMetadata.value.pageSize)
                .collectLatest { response ->
                    _olympiads.value = response.items
                    _paginationMetadata.value = response.meta
                    _isLoading.value = false
                }
        }
    }

    fun onPageChanged(newPage: Int) {
        _currentPage.value = newPage
        loadOlympiads()
    }

    fun onPageSizeChanged(newSize: Int) {
        _paginationMetadata.value = _paginationMetadata.value.copy(pageSize = newSize)
        _currentPage.value = 1 // Сбрасываем на первую страницу при изменении размера
        loadOlympiads()
    }

    fun refreshData() {
        _currentPage.value = 1
        loadOlympiads()
    }
}