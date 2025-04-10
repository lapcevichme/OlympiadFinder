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

/*
    Необходимо подумать насчет полей.
    Вероятно keywords даже не нужны и будут импользоваться только на сервере
    Можно сделать так:
    responseOlympiad - это олимпиада которая содержит данных по минимуму (или наоборот)
    olympiad - подробные данные, например в новом экране.
 */