package com.lapcevichme.olympiadfinder.presentation.viewmodel

/**
 * Data Class для параметров загрузки олимпиад.
 * Используется для объединения всех параметров, влияющих на запрос к API.
 *
 * @property page Текущий номер страницы.
 * @property pageSize Размер страницы (количество элементов на странице).
 * @property query Поисковый запрос (строка).
 * @property selectedGrades Список выбранных классов для фильтрации.
 * @property selectedSubjects Список ID выбранных предметов для фильтрации.
 */
data class LoadParams(
    val page: Int,
    val pageSize: Int,
    val query: String,
    val selectedGrades: List<Int>,
    val selectedSubjects: List<Long>
)
