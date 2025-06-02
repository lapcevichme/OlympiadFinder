package com.lapcevichme.olympiadfinder.data.network.model

import com.lapcevichme.olympiadfinder.domain.model.Subject

/**
 * Data class representing the network response for a Subject.
 */
data class NetworkSubject(
    val id: Long,
    val name: String
)

/**
 * Extension function to map a [NetworkSubject] to the domain model [Subject].
 */
fun NetworkSubject.toDomain(): Subject {
    return Subject(
        id = this.id,
        name = this.name
    )
}