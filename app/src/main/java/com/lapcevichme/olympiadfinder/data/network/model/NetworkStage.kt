package com.lapcevichme.olympiadfinder.data.network.model

import com.google.gson.annotations.SerializedName
import com.lapcevichme.olympiadfinder.domain.model.Stage
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Data-класс, представляющий сетевой ответ для этапа олимпиады.
 * Используется для десериализации данных об этапах из сетевых запросов.
 */
data class NetworkStage(
    val name: String,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?
)

/**
 * Расширяющая функция для преобразования [NetworkStage] в доменную модель [Stage].
 * Производит парсинг строковых дат в объекты [LocalDate], обрабатывая возможные ошибки парсинга.
 *
 * @return Доменная модель [Stage], представляющая данный сетевой этап.
 */
fun NetworkStage.toDomain(): Stage {
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    return Stage(
        name = name,
        startDate = startDate?.let { runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull() },
        endDate = endDate?.let { runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull() }
    )
}