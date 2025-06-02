package com.lapcevichme.olympiadfinder.data.network.model

import com.google.gson.annotations.SerializedName
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

data class NetworkSubject(
    val id: Long,
    val name: String
)

data class NetworkStage(
    val name: String,
    @SerializedName("start_date") val startDate: String?, // Или LocalDate
    @SerializedName("end_date") val endDate: String?
)

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

fun NetworkStage.toDomain(): Stage {
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    return Stage(
        name = name,
        startDate = startDate?.let { runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull() },
        endDate = endDate?.let { runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull() }
    )
}

fun NetworkSubject.toDomain(): Subject {
    return Subject(
        id = this.id,
        name = this.name
    )
}