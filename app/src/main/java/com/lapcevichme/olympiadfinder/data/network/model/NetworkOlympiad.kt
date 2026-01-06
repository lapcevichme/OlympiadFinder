package com.lapcevichme.olympiadfinder.data.network.model

import com.google.gson.annotations.SerializedName
import com.lapcevichme.olympiadfinder.domain.model.Olympiad

/**
 * Data-класс, представляющий сетевой ответ для сущности [Olympiad].
 * Используется для десериализации данных об олимпиаде из сетевых запросов.
 */
data class NetworkOlympiad(
    val id: Long,
    val name: String,
    val subjects: List<NetworkSubject>?,
    @SerializedName("min_grade") val minGrade: Int?,
    @SerializedName("max_grade") val maxGrade: Int?,
    val stages: List<NetworkStage>?,
    val link: String?,
    val description: String?,
    val keywords: String?
)

/**
 * Расширяющая функция для преобразования [NetworkOlympiad] в доменную модель [Olympiad].
 * Производит маппинг полей, включая преобразование вложенных списков.
 *
 * @return Доменная модель [Olympiad], представляющая данную сетевую сущность.
 */
fun NetworkOlympiad.toDomain(): Olympiad {
    return Olympiad(
        id = id,
        name = name,
        subjects = subjects?.map { it.toDomain() } ?: emptyList(),
        minGrade = minGrade,
        maxGrade = maxGrade,
        stages = stages?.map { it.toDomain() } ?: emptyList(),
        link = link,
        description = description,
        keywords = keywords
    )
}