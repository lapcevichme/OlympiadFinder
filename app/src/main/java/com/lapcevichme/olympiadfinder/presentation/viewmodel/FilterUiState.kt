package com.lapcevichme.olympiadfinder.presentation.viewmodel

/**
 * Data Class для состояния UI фильтров.
 * Используется для временного хранения выбранных фильтров в Bottom Sheet,
 * до их применения.
 *
 * @property selectedGrades Список выбранных классов в UI фильтров.
 * @property selectedSubjects Список ID выбранных предметов в UI фильтров.
 */
data class FilterUiState(
    val selectedGrades: List<Int> = emptyList(),
    val selectedSubjects: List<Long> = emptyList()
)
