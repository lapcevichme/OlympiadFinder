package com.lapcevichme.olympiadfinder.presentation.viewmodel

data class FilterUiState(
    val selectedGrades: List<Int> = emptyList()
    // TODO: Добавить selectedSubjects: List<Subject>
)
