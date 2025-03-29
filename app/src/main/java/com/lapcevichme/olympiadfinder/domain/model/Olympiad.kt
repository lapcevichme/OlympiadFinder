package com.lapcevichme.olympiadfinder.domain.model

data class Olympiad(
    val id: Long,
    val name: String,
    val subjects: List<Subject>?,
    val minGrade: Int?,
    val maxGrade: Int?,
    val stages: List<Stage>?,
    val link: String?,
    val description: String?,
    val keywords: String?
)