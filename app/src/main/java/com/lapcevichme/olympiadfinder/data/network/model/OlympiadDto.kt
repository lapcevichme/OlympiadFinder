package com.lapcevichme.olympiadfinder.data.network.model

import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.squareup.moshi.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class NetworkOlympiad(
    val id: Long,
    val name: String,
    val subjects: List<NetworkSubject>?,
    @Json(name = "min_grade") val minGrade: Int?,
    @Json(name = "max_grade") val maxGrade: Int?,
    val stages: List<NetworkStage>?,
    val link: String?,
    val description: String?,
    val keywords: String?
)

data class NetworkSubject(
    val name: String
)

data class NetworkStage(
    val name: String,
    @Json(name = "start_date") val startDate: String?, // Или LocalDate
    @Json(name = "end_date") val endDate: String?
)

private fun NetworkOlympiad.toDomain(): Olympiad {
    return Olympiad(
        id = id,
        name = name,
        subjects = subjects?.map { Subject(it.name) } ?: emptyList(),
        minGrade = minGrade,
        maxGrade = maxGrade,
        stages = stages?.map { it.toDomain() } ?: emptyList(),
        link = link,
        description = description,
        keywords = keywords
    )
}


private fun NetworkStage.toDomain(): Stage {
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    return Stage(
        name = name,
        startDate = startDate?.let { runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull() },
        endDate = endDate?.let { runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull() }
    )
}
