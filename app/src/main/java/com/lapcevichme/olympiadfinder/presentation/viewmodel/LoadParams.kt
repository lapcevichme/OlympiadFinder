package com.lapcevichme.olympiadfinder.presentation.viewmodel

data class LoadParams(
    val page: Int,
    val pageSize: Int,
    val query: String,
    val selectedGrades: List<Int>
    // TODO: добавить selectedSubjects: List<Subject>
)
