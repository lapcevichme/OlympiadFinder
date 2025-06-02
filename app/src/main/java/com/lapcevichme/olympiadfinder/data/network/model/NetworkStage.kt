package com.lapcevichme.olympiadfinder.data.network.model

import com.google.gson.annotations.SerializedName
import com.lapcevichme.olympiadfinder.domain.model.Stage
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Data class representing the network response for a Stage of an Olympiad.
 */
data class NetworkStage(
    val name: String,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?
)

/**
 * Extension function to map a [NetworkStage] to the domain model [Stage].
 */
fun NetworkStage.toDomain(): Stage {
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    return Stage(
        name = name,
        startDate = startDate?.let { runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull() },
        endDate = endDate?.let { runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull() }
    )
}