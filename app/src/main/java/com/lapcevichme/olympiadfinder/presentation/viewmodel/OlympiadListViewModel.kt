package com.lapcevichme.olympiadfinder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.usecases.GetAllOlympiadsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OlympiadListViewModel @Inject constructor(
    private val getAllOlympiadsUseCase: GetAllOlympiadsUseCase
) : ViewModel() {

    private val _olympiads = MutableStateFlow<List<Olympiad>>(emptyList())
    val olympiads: StateFlow<List<Olympiad>> = _olympiads

    init {
        loadOlympiads()
    }

    private fun loadOlympiads() {
        viewModelScope.launch {
            getAllOlympiadsUseCase().collectLatest {
                _olympiads.value = it
            }
        }
    }
}