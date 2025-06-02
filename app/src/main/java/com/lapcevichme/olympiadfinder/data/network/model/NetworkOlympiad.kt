package com.lapcevichme.olympiadfinder.data.network.model

import com.google.gson.annotations.SerializedName
import com.lapcevichme.olympiadfinder.domain.model.Olympiad

/**
 * Data class representing the network response for an Olympiad.
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
 * Extension function to map a [NetworkOlympiad] to the domain model [Olympiad].
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