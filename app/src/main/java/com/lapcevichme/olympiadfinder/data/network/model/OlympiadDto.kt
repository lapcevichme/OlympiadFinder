package com.lapcevichme.olympiadfinder.data.network.model

import com.squareup.moshi.Json

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
