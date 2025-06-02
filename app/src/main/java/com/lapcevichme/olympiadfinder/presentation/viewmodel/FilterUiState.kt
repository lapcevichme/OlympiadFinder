package com.lapcevichme.olympiadfinder.presentation.viewmodel

data class FilterUiState(
    val selectedGrades: List<Int> = emptyList(),
    val selectedSubjects: List<Long> = emptyList()
)
